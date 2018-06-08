package com.dawnimpulse.wallchange.services

import android.app.IntentService
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log
import android.view.Display
import android.view.WindowManager
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.dawnimpulse.wallchange.network.RequestResponse
import com.dawnimpulse.wallchange.network.VolleyWrapper
import com.dawnimpulse.wallchange.utils.C
import com.dawnimpulse.wallchange.utils.Config
import com.pixplicity.easyprefs.library.Prefs
import org.json.JSONObject

/**
 * Created by Saksham on 2018 01 15
 * Last Branch Update - v
 * Updates :
 * Initial - v - Saksham - 2018 01 15
 */
class ChangeWallpaper : IntentService, RequestResponse {
    lateinit var volley: VolleyWrapper
    lateinit var intent: Intent

    /**
     * Default Constructor
     */
    constructor() : super("ChangeWallpaper") {
    }

    override fun onHandleIntent(intent: Intent) {
        this.intent = intent
        volley = VolleyWrapper(this)
        volley.getCall("https://api.unsplash.com/photos/random?featured&client_id=${Config.UNSPLASH_API_KEY}", 2)
        volley.setListener(this)
    }

    override fun onErrorResponse(volleyError: VolleyError, callback: Int) {
        Log.d("Test", volleyError.toString())
        stopService(intent)
    }

    override fun onResponse(response: JSONObject, callback: Int) {
        if (callback == 2) {
            val urls = response.getJSONObject("urls")
            val links = response.getJSONObject("links");
            /*val params = HashMap<String, String>()
            params.put("value1", urls.getString("raw") + "?h=1080")*/

            Glide.with(this)
                    .load(urls.getString("raw") + "?h=1080")
                    .asBitmap()
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                            if (resource != null) {
                                val wallpaperManager = WallpaperManager.getInstance(this@ChangeWallpaper)
                                wallpaperManager.setBitmap(bitmapCropper(resource!!))
                                Prefs.putString(C.CURRENT, urls.getString("raw") + "?h=1080");
                                Prefs.putString(C.ID, links.getString("html") + C.UTM_PARAMETERS);
                                stopService(intent)
                            } else
                                stopService(intent)
                        }
                    })
        } else
            stopService(intent)
    }

    /**
     * Handling of bitmap cropping based on device screen
     * Could be used in future for external cropping too
     *
     * @param originalBitmap
     * @return
     */
    fun bitmapCropper(originalBitmap: Bitmap): Bitmap? {

        val scaleHcf: Int
        val scaleX: Int
        val scaleY: Int
        val originalWidth: Int
        val originalHeight: Int
        var width = 0
        var height = 0

        val point: Point
        val mWindowManager: WindowManager
        val display: Display
        var modifiedBitmap: Bitmap? = null
        val scaledBitmap: Bitmap? = null

        point = Point()
        mWindowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        display = mWindowManager.defaultDisplay
        display.getSize(point) //The point now has display dimens

        originalWidth = originalBitmap.width
        originalHeight = originalBitmap.height
        scaleHcf = calculateHcf(point.x, point.y)

        // If bitmap is null or some other problem
        if (originalWidth == 0) {
            return null
        }

        /* Get X & Y scaling increment factor
    *  If ratio i.e. hcf is less than 20 then use it else divide it by 8
    */
        scaleX = if (point.x / scaleHcf > 20) point.x / scaleHcf / 8 else point.x / scaleHcf
        scaleY = if (point.y / scaleHcf > 20) point.y / scaleHcf / 8 else point.y / scaleHcf

        //Loop while incrementing width and height by scaling factors
        while (width < originalWidth && height < originalHeight) {
            width += scaleX
            height += scaleY
        }

        //Decrease one scaling factor so it wont exceed the max bitmap length
        width -= scaleX
        height -= scaleY

        //Get the starting point to crop the original Bitmap
        var startingPointX = (originalWidth - width) / 2
        var startingPointY = (originalHeight - height) / 2

        // if we get starting point less than 0 then make it 0
        startingPointX = if (startingPointX < 0) 0 else startingPointX
        startingPointY = if (startingPointY < 0) 0 else startingPointY

        //Create cropped version of original bitmap
        modifiedBitmap = Bitmap.createBitmap(originalBitmap, startingPointX, startingPointY, width, height)
        //Create final scaled bitmap based on exact screen size
        val finalBitmap = Bitmap.createScaledBitmap(modifiedBitmap!!, point.x, point.y, false)

        originalBitmap.recycle()
        modifiedBitmap.recycle()

        return finalBitmap
    }

    /**
     * Get hcf of width & height of an image
     *
     * @param width  - Width of device
     * @param height - Height of device
     * @return
     */
    private fun calculateHcf(width: Int, height: Int): Int {
        var width = width
        var height = height
        while (height != 0) {
            val t = height
            height = width % height
            width = t
        }
        return width
    }
}