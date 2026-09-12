package com.example.panicbutton.domain.model

sealed class PanicState {
    data object Idle : PanicState()

    data class Confirming(val secondsLeft: Int) : PanicState()

    data object Searching : PanicState()

    data class RequestSent(
        val helper: MockUser,
        val distanceKm: Double,
        val secondsLeft: Int
    ) : PanicState()

    data class Accepted(
        val helper: MockUser,
        val distanceKm: Double
    ) : PanicState()

    data class Confirmed(
        val helper: MockUser,
        val distanceKm: Double,
        val etaMinutes: Int
    ) : PanicState()

    data object AllDeclined : PanicState()
}
