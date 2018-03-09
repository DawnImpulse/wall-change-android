package com.dawnimpulse.wallchange

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Toast
import com.dawnimpulse.wallchange.services.ChangeWallpaper

class RefreshActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        callIt()
    }

    private fun callIt() {
        val view = layoutInflater.inflate(R.layout.custom_toast, findViewById<ViewGroup>(R.id.customToastLayout))
        val toast = Toast(this);
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
        startService(Intent(this, ChangeWallpaper::class.java))
        finish()
    }
}
