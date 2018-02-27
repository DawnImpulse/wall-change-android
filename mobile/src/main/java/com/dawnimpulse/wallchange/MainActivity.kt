package com.dawnimpulse.wallchange

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.dawnimpulse.wallchange.services.ChangeWallpaper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        callIt()
    }

    fun callIt() {
        Toast.makeText(this, "Wallpaper is Changing", Toast.LENGTH_SHORT).show()
        startService(Intent(this, ChangeWallpaper::class.java))
        finish()
    }
}
