package com.dawnimpulse.wallchange.utils

import android.app.Application
import android.content.ContextWrapper
import android.util.Log
import com.dawnimpulse.wallchange.BuildConfig
import com.dawnimpulse.wallchange.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
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

        setUpRemoteConfig()

    }

    private fun setUpRemoteConfig() {
        var cacheExpiration: Long = 3600 // 1 hour in seconds.
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()

        if (mFirebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }

        Config.UNSPLASH_API_KEY = mFirebaseRemoteConfig.getString(C.UNSPLASH_API_KEY)
        mFirebaseRemoteConfig.setConfigSettings(configSettings)
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_defaults)
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mFirebaseRemoteConfig.activateFetched()
                        Config.UNSPLASH_API_KEY = mFirebaseRemoteConfig.getString(C.UNSPLASH_API_KEY)
                        Log.d("Test",Config.UNSPLASH_API_KEY)
                    } else
                        Log.d("Test", "Fetch failed")
                }

    }
}