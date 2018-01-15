package com.dawnimpulse.wallchange.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


/**
 * Created by Saksham on 2018 01 15
 * Last Branch Update - master
 * Updates :
 * Initial - master - Saksham - 2018 01 15
 */

class VolleyWrapper(private val context: Context) {
    private var listener: RequestResponse? = null
    private var requestQueue: RequestQueue = Volley.newRequestQueue(context)

    fun setListener(listener: RequestResponse) {
        this.listener = listener
    }

    fun postCall(URL: String, params: HashMap<String, String>, callbackID: Int) {
        val customPostRequest = JsonObjectRequest(Request.Method.POST,
                URL, JSONObject(params), Response.Listener { response ->
            //successful response
            listener!!.onResponse(response, callbackID) //onCallback
        }, Response.ErrorListener { error ->
            //error
            listener!!.onErrorResponse(error, callbackID) //onCallback
        })

        requestQueue.add(customPostRequest)
    }

    fun getCall(URL: String, callbackID: Int) {
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                URL, null, Response.Listener { response ->
            //successful response
            listener!!.onResponse(response, callbackID) //onCallback
        }, Response.ErrorListener { error ->
            //error
            listener!!.onErrorResponse(error, callbackID) //onCallback
        })

        requestQueue.add(jsonObjectRequest)
    }
}