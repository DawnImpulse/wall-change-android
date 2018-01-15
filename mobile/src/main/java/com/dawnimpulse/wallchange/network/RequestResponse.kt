package com.dawnimpulse.wallchange.network

import com.android.volley.VolleyError
import org.json.JSONObject

/**
 * Created by Saksham on 2018 01 15
 * Last Branch Update - master
 * Updates :
 * Initial - master - Saksham - 2018 01 15
 */

interface RequestResponse {
    fun onErrorResponse(volleyError: VolleyError, callback: Int)
    fun onResponse(response: JSONObject, callback: Int)
}