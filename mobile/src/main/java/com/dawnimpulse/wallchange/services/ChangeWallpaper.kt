package com.dawnimpulse.wallchange.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.android.volley.VolleyError
import com.dawnimpulse.wallchange.network.RequestResponse
import com.dawnimpulse.wallchange.network.VolleyWrapper
import org.json.JSONObject

/**
 * Created by Saksham on 2018 01 15
 * Last Branch Update - v
 * Updates :
 * Initial - v - Saksham - 2018 01 15
 */
class ChangeWallpaper : IntentService, RequestResponse {
    lateinit var volley: VolleyWrapper
    lateinit var intent:Intent

    /**
     * Default Constructor
     */
    constructor() : super("ChangeWallpaper") {
    }

    override fun onHandleIntent(intent: Intent) {
        this.intent = intent
        volley = VolleyWrapper(this)
        volley.getCall("https://api.unsplash.com/photos/random?client_id=a25247a07df2c569f6f3dc129f43b0eb3b0e3ff69b00d5b84dd031255e55b961", 2)
        volley.setListener(this)
    }

    override fun onErrorResponse(volleyError: VolleyError, callback: Int) {
        Log.d("Test", volleyError.toString())
        stopService(intent)
    }

    override fun onResponse(response: JSONObject, callback: Int) {
        if (callback == 2) {
            val urls = response.getJSONObject("urls")
            val params = HashMap<String, String>()
            params.put("value1", urls.getString("raw") + "?h=1080")
            Log.d("Test", params.toString())
            volley.postCall("https://maker.ifttt.com/trigger/wall-change/with/key/P-QrqAhCo3hGCPUYvcb3i", params, 1)
        } else {
            stopService(intent)
        }
    }
}