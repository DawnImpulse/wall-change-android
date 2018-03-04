package com.dawnimpulse.wallchange.utils

import android.app.Application
import android.content.ContextWrapper
import com.pixplicity.easyprefs.library.Prefs




/**
 * Created by Saksham on 2018 01 15
 * Last Branch Update - master
 * Updates :
 * Saksham - 2018 03 04 - master - Prefs library
 * Saksham - 2018 01 15 - master - Initial
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()
    }
}