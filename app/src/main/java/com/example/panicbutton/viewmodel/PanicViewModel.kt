package com.example.panicbutton.viewmodel

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.panicbutton.data.local.RequestHistoryEntity
import com.example.panicbutton.data.repository.PanicRepository
import com.example.panicbutton.domain.model.MockUser
import com.example.panicbutton.domain.model.PanicState
import com.example.panicbutton.domain.util.DistanceCalculator
import com.example.panicbutton.notification.NotificationActionReceiver
import com.example.panicbutton.notification.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PanicViewModel(
    private val repository: PanicRepository,
    private val notificationHelper: NotificationHelper,
    private val appContext: Context
) : ViewModel() {

    private val _panicState = MutableStateFlow<PanicState>(PanicState.Idle)
    val panicState: StateFlow<PanicState> = _panicState.asStateFlow()

    val users: StateFlow<List<MockUser>> = repository.users

    private val _historyFilter = MutableStateFlow("All")
    val historyFilter: StateFlow<String> = _historyFilter.asStateFlow()

    val history: StateFlow<List<RequestHistoryEntity>> = repository.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showInAppDialog = MutableStateFlow(false)
    val showInAppDialog: StateFlow<Boolean> = _showInAppDialog.asStateFlow()

    private val _dialogHelperName = MutableStateFlow("")
    val dialogHelperName: StateFlow<String> = _dialogHelperName.asStateFlow()

    private var countdownJob: Job? = null
    private var timeoutJob: Job? = null
    private var searchJob: Job? = null
    private var priorityQueue: MutableList<Pair<MockUser, Double>> = mutableListOf()
    private var currentHelperIndex = 0
    private var responseHandled = false

    init {
        NotificationActionReceiver.onResponse = { accepted ->
            onHelperResponse(accepted)
        }
    }

    override fun onCleared() {
        super.onCleared()
        NotificationActionReceiver.onResponse = null
        cancelAllJobs()
    }

    fun onPanicPressed() {
        if (_panicState.value !is PanicState.Idle) return

        _panicState.value = PanicState.Confirming(5)
        countdownJob = viewModelScope.launch {
            for (i in 5 downTo 1) {
                _panicState.value = PanicState.Confirming(i)
                delay(1000)
            }
            onConfirm()
        }
    }

    fun onCancelPanic() {
        countdownJob?.cancel()
        countdownJob = null
        _panicState.value = PanicState.Idle
    }

    fun onRetry() {
        _panicState.value = PanicState.Searching
        startSearching()
    }

    fun onDone() {
        notificationHelper.cancelNotification()
        _showInAppDialog.value = false
        _panicState.value = PanicState.Idle
    }

    fun onHelperResponse(accepted: Boolean) {
        if (responseHandled) return
        responseHandled = true

        timeoutJob?.cancel()
        timeoutJob = null

        _showInAppDialog.value = false
        notificationHelper.cancelNotification()

        val state = _panicState.value
        if (state !is PanicState.RequestSent) return

        viewModelScope.launch {
            if (accepted) {
                repository.logRequest(
                    helperName = state.helper.name,
                    distanceKm = state.distanceKm,
                    outcome = "Accepted"
                )
                _panicState.value = PanicState.Accepted(state.helper, state.distanceKm)
                delay(300)
                val eta = DistanceCalculator.calculateEtaMinutes(state.distanceKm)
                _panicState.value = PanicState.Confirmed(state.helper, state.distanceKm, eta)
            } else {
                repository.logRequest(
                    helperName = state.helper.name,
                    distanceKm = state.distanceKm,
                    outcome = "Declined"
                )
                moveToNextHelper()
            }
        }
    }

    fun toggleUserOnline(userId: String) {
        repository.toggleUserOnlineStatus(userId)
    }

    fun setHistoryFilter(filter: String) {
        _historyFilter.value = filter
    }

    fun getFilteredHistory(allHistory: List<RequestHistoryEntity>, filter: String): List<RequestHistoryEntity> {
        return if (filter == "All") allHistory
        else allHistory.filter { it.outcome == filter }
    }

    private fun onConfirm() {
        countdownJob = null
        startSearching()
    }

    private fun startSearching() {
        _panicState.value = PanicState.Searching
        priorityQueue = repository.getOnlineHelpersByDistance().toMutableList()
        currentHelperIndex = 0

        searchJob = viewModelScope.launch {
            delay(800)
            sendToNextHelper()
        }
    }

    private fun sendToNextHelper() {
        if (currentHelperIndex >= priorityQueue.size) {
            _panicState.value = PanicState.AllDeclined
            return
        }

        val (helper, distance) = priorityQueue[currentHelperIndex]
        responseHandled = false

        _panicState.value = PanicState.RequestSent(helper, distance, 8)

        triggerVibration()

        val me = repository.getMe()
        notificationHelper.showHelpRequestNotification(helper.name, me.name)

        _dialogHelperName.value = helper.name
        _showInAppDialog.value = true

        timeoutJob = viewModelScope.launch {
            for (i in 8 downTo 1) {
                if (responseHandled) return@launch
                val currentState = _panicState.value
                if (currentState is PanicState.RequestSent && currentState.helper.id == helper.id) {
                    _panicState.value = PanicState.RequestSent(helper, distance, i)
                }
                delay(1000)
            }
            if (!responseHandled) {
                responseHandled = true
                _showInAppDialog.value = false
                notificationHelper.cancelNotification()
                repository.logRequest(
                    helperName = helper.name,
                    distanceKm = distance,
                    outcome = "Timed Out"
                )
                moveToNextHelper()
            }
        }
    }

    private fun moveToNextHelper() {
        currentHelperIndex++
        viewModelScope.launch {
            _panicState.value = PanicState.Searching
            delay(600)
            sendToNextHelper()
        }
    }

    private fun triggerVibration() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            val timings = longArrayOf(0, 100, 100, 300, 100, 100)
            val amplitudes = intArrayOf(0, 200, 0, 255, 0, 200)
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } catch (_: Exception) {
        }
    }

    private fun cancelAllJobs() {
        countdownJob?.cancel()
        timeoutJob?.cancel()
        searchJob?.cancel()
    }

    class Factory(
        private val repository: PanicRepository,
        private val notificationHelper: NotificationHelper,
        private val appContext: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PanicViewModel(repository, notificationHelper, appContext) as T
        }
    }
}
