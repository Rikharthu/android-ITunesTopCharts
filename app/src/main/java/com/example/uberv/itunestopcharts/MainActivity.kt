package com.example.uberv.itunestopcharts

import android.app.Activity
import android.arch.lifecycle.Observer
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.uberv.itunestopcharts.api.ITunesApiService
import com.example.uberv.itunestopcharts.api.RestAPI
import com.example.uberv.itunestopcharts.api.models.*
import com.example.uberv.itunestopcharts.utils.createBlurryBackground
import com.example.uberv.itunestopcharts.utils.readFromAssets
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.DateFormat
import javax.inject.Inject


class MainActivity : AnalyticsActivity() {

    val component by lazy { app.component }
    var mMediaPlayer: MediaPlayer? = null
    @Inject
    lateinit var apiService: ITunesApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

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
                recycler.addItemDecoration(SimpleDividerItemDecoration(this@MainActivity).apply {
                    setTint(220, 255, 255, 255)
                })
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

                    logMusicPlayAnalytics(it)

                    val imageView = findViewById<ImageView>(R.id.background_image)
                    var image: Image? = null
                    it.image!!.forEach {
                        if (image == null || image!!.height!! < it.height!!) {
                            image = it
                        }
                    }

                    val futureTarget = Glide.with(this@MainActivity).asBitmap().load(image!!.url)
                            .submit()
                    Thread {
                        val image = createBlurryBackground(futureTarget.get(), imageView.width, imageView.height, this@MainActivity)
                        runOnUiThread {
                            imageView.setImageBitmap(image)
                        }
                    }.start()
                })
                recycler.adapter = feedAdapter
            }

            override fun onFailure(call: Call<Feed>?, t: Throwable?) {
                Timber.e(t)
            }

        })
//        Glide.with(this@MainActivity).load(imageUrl).into(imageView)


        Timber.d(feedJson)
    }

    private fun logMusicPlayAnalytics(entry: Entry) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, entry.id.toString())
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, entry.title)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "music_preview")
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    // Extension property
    val Activity.app: App
        get() = application as App
}
