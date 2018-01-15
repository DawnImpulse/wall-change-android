package com.dawnimpulse.wallchange.utils

import android.app.Application
import shortbread.Shortbread


/**
 * Created by Saksham on 2018 01 15
 * Last Branch Update - master
 * Updates :
 * Saksham - 2018 01 15 - master - Initial
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Shortbread.create(this)
    }
}