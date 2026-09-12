package com.example.panicbutton

import android.app.Application
import com.example.panicbutton.data.local.AppDatabase
import com.example.panicbutton.data.mock.MockUserDataSource
import com.example.panicbutton.data.repository.PanicRepository
import com.example.panicbutton.notification.NotificationHelper

class PanicButtonApp : Application() {

    lateinit var repository: PanicRepository
        private set

    lateinit var notificationHelper: NotificationHelper
        private set

    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getInstance(this)
        val mockUserDataSource = MockUserDataSource()

        repository = PanicRepository(mockUserDataSource, database)
        notificationHelper = NotificationHelper(this)
    }
}
