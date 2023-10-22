package com.example.firebaseauthui

import android.app.Application
import com.example.firebaseauthui.fcm.VolleyHelper

class TCAPartnerApplication: Application() {
    companion object{
        lateinit var volleyHelper: VolleyHelper
    }

    override fun onCreate() {
        super.onCreate()
        volleyHelper = VolleyHelper.getInstance(this)
    }
}