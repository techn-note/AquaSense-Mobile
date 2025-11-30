package com.technote.aquasense

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AquasenseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}