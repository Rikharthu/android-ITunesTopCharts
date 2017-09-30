package com.example.uberv.itunestopcharts

import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.Element
import android.support.v8.renderscript.RenderScript
import android.support.v8.renderscript.ScriptIntrinsicBlur
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.uberv.itunestopcharts.api.RestAPI
import com.example.uberv.itunestopcharts.api.models.Feed
import com.example.uberv.itunestopcharts.api.models.FeedDeserializer
import com.example.uberv.itunestopcharts.utils.readFromAssets
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.DateFormat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
        //TODO for debug
        findViewById<Button>(R.id.crash_btn).setOnClickListener { throw RuntimeException("This is a crash!") }

        val feedJson = readFromAssets("topcharts.json", this)

        val gson = GsonBuilder()
                .setLenient()
                .registerTypeAdapter(
                        Feed::class.java, FeedDeserializer()
                )
                .setDateFormat(DateFormat.FULL, DateFormat.FULL)
                .create()
        val feed = gson.fromJson(feedJson, Feed::class.java)

        val restApi = RestAPI()
        restApi.getHotTracks().enqueue(object : Callback<Feed> {
            override fun onResponse(call: Call<Feed>?, response: Response<Feed>?) {
                Timber.d(response?.body().toString())
                val feed = response?.body()
//                val inputBitmap = imageView.drawable.g
                val previewUrl = feed?.entry?.get(1)?.link?.get(1)?.href
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer.setDataSource(previewUrl)
                mediaPlayer.prepare() // might take long! (for buffering, etc)
                mediaPlayer.start()
            }

            override fun onFailure(call: Call<Feed>?, t: Throwable?) {
                Timber.e(t)
            }

        })

        val textView = findViewById<TextView>(R.id.textview)
        textView.isSelected = true


        val imageUrl = "http://is1.mzstatic.com/image/thumb/Music91/v4/b4/7b/02/b47b02f9-58c7-dd3a-c97e-6544bd9ad36a/UMG_cvrart_00602557680850_01_RGB72_1800x1800_17UMGIM98210.jpg/170x170bb-85.jpg";
        val imageView = findViewById<ImageView>(R.id.background_image)
        val futureTarget = Glide.with(this).asBitmap().load(imageUrl)
                .submit()
        Thread {
            val image = Bitmap.createScaledBitmap(futureTarget.get(), imageView.width, imageView.height, false)
            val blurRadius = 25f
            val rs = RenderScript.create(this@MainActivity)
            val blurry = Bitmap.createBitmap(image)
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            val input = Allocation.createFromBitmap(rs, image)
            val output = Allocation.createFromBitmap(rs, blurry)
            blurScript.setRadius(blurRadius)
            blurScript.setInput(input)
            blurScript.forEach(output)
            output.copyTo(blurry)

            runOnUiThread {
                imageView.setImageBitmap(blurry)
            }
        }.start()
//        Glide.with(this@MainActivity).load(imageUrl).into(imageView)


        Timber.d(feedJson)
    }
}
