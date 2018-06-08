package com.dawnimpulse.wallchange

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.Toast
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.dawnimpulse.wallchange.network.RequestResponse
import com.dawnimpulse.wallchange.network.VolleyWrapper
import com.dawnimpulse.wallchange.utils.C
import com.dawnimpulse.wallchange.utils.Config
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

/**
 * Created by DawnImpulse
 */
class MainActivity : AppCompatActivity(), RequestResponse {

    private lateinit var volley: VolleyWrapper
    private lateinit var view: View
    private lateinit var toast: Toast

    /**
     * On create
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view = layoutInflater.inflate(R.layout.custom_toast, findViewById<ViewGroup>(R.id.customToastLayout))
        toast = Toast(this);
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view

        if (Prefs.contains(C.CURRENT)) {
            val url = Prefs.getString(C.CURRENT, "");
            Glide.with(this)
                    .load(url)
                    .into(image);
        } else {
            toast.show()
            reload()
        }

        fab.setOnClickListener {
            toast.show()
            reload()
        }

    }

    /**
     * Reloading our wallpaper
     */
    private fun reload() {
        volley = VolleyWrapper(this)
        volley.getCall("https://api.unsplash.com/photos/random?featured&client_id=${Config.UNSPLASH_API_KEY}",
                1)
        volley.setListener(this)
    }

    /**
     * Error Response
     * @param volleyError
     * @param callback
     */
    override fun onErrorResponse(volleyError: VolleyError, callback: Int) {
        Log.d("Test", volleyError.toString());
        Toast.makeText(this, volleyError.toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * On Response
     * @param response
     * @param callback
     */
    override fun onResponse(response: JSONObject, callback: Int) {
        if (callback == 1) {
            val urls = response.getJSONObject("urls")
            val links = response.getJSONObject("links");

            Glide.with(this)
                    .load(urls.getString("raw") + "?h=1080")
                    .into(image);

            Glide.with(this)
                    .load(urls.getString("raw") + "?h=1080")
                    .asBitmap()
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                            if (resource != null) {
                                val wallpaperManager = WallpaperManager.getInstance(this@MainActivity)
                                wallpaperManager.setBitmap(bitmapCropper(resource!!))
                                Prefs.putString(C.CURRENT, urls.getString("raw") + "?h=1080");
                                Prefs.putString(C.ID, links.getString("html") + C.UTM_PARAMETERS);
                            } else
                                Toast.makeText(this@MainActivity, "Empty Resource", Toast.LENGTH_SHORT).show()
                        }
                    })
        }
    }

    /**
     * On create options menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * On options item selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.unsplash) {
            if (Prefs.contains(C.ID)) {
                val url = Prefs.getString(C.ID, "https://unsplash.com");
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } else {
                Toast.makeText(this, "No image available", Toast.LENGTH_SHORT);
            }
        }
        return true;
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
