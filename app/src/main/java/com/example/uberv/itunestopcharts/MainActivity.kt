package com.example.uberv.itunestopcharts

import android.app.Activity
import android.arch.lifecycle.Observer
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.Element
import android.support.v8.renderscript.RenderScript
import android.support.v8.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.uberv.itunestopcharts.api.ITunesApiService
import com.example.uberv.itunestopcharts.api.RestAPI
import com.example.uberv.itunestopcharts.api.models.ApiResponse
import com.example.uberv.itunestopcharts.api.models.Feed
import com.example.uberv.itunestopcharts.api.models.FeedDeserializer
import com.example.uberv.itunestopcharts.utils.readFromAssets
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.DateFormat
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    val component by lazy { app.component }

    var mMediaPlayer: MediaPlayer? = null
    @Inject
    lateinit var apiService: ITunesApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

        component.inject(this)

        // TODO for debug
        val apiResponse = apiService.getTopTracks(7)
        apiResponse.observe(this, Observer<ApiResponse<Feed>> {
            Timber.d("onChanged")
        })


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

                val recycler = findViewById<RecyclerView>(R.id.recycler)
                recycler.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayout.VERTICAL, false)
                val feedAdapter = TrackItemAdapter(feed!!.entry!!, {
                    Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT)
                    val previewUrl = it.link?.get(1)?.href
                    if (mMediaPlayer != null) {
                        mMediaPlayer?.stop()
                        mMediaPlayer?.release()
                    }
                    mMediaPlayer = MediaPlayer()
                    mMediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mMediaPlayer?.setDataSource(previewUrl)
                    mMediaPlayer?.prepare() // might take long! (for buffering, etc)
                    mMediaPlayer?.start()
                })
                recycler.adapter = feedAdapter
            }

            override fun onFailure(call: Call<Feed>?, t: Throwable?) {
                Timber.e(t)
            }

        })

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
            blurScript.setInput(output)
            blurScript.forEach(output)
            blurScript.setInput(output)
            blurScript.forEach(output)
            output.copyTo(blurry)

            runOnUiThread {
                imageView.setImageBitmap(blurry)
            }
        }.start()
//        Glide.with(this@MainActivity).load(imageUrl).into(imageView)


        Timber.d(feedJson)
    }

    // Extension property
    val Activity.app: App
        get() = application as App
}
