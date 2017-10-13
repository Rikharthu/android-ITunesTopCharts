package com.example.uberv.itunestopcharts

import android.app.Activity
import android.arch.lifecycle.Observer
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.uberv.itunestopcharts.api.ITunesApiService
import com.example.uberv.itunestopcharts.api.RestAPI
import com.example.uberv.itunestopcharts.api.models.*
import com.example.uberv.itunestopcharts.utils.readFromAssets
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
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
//                recycler.addItemDecoration(SimpleDividerItemDecoration(this@MainActivity).apply {
//                    setTint(220, 255, 255, 255)
//                })
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
                        val original = futureTarget.get()

                        val outBitmap = original.copy(original.config, true)

                        //Create the RenderScript context
                        val rs = RenderScript.create(this@MainActivity)

                        //Create allocations for input and output data
                        val input = Allocation.createFromBitmap(rs, original,
                                Allocation.MipmapControl.MIPMAP_NONE,
                                Allocation.USAGE_SCRIPT)
                        val output = Allocation.createTyped(rs, input.getType())

                        //Run a blur at the maximum supported radius (25f)
                        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
                        script.setRadius(4f)
                        script.setInput(input)
                        script.forEach(output)
                        script.setInput(output)
                        script.forEach(output)
                        output.copyTo(outBitmap)

                        //Tear down the RenderScript context
                        rs.destroy()

                        runOnUiThread {
                            //                            imageView.setImageBitmap(image)
                            background_slide.setImagePair(original, outBitmap)
                        }
                    }.start()
                })
                recycler.adapter = feedAdapter

                // TODO for debug
                recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        Timber.d("onScrolled: $dx $dy")

                        val firstItem = recyclerView.getChildAt(0)
                        if (firstItem.tag == "HEADER") {
                            // first item is header, thus it is still visible
                            val topOffset = firstItem.top + firstItem.height
                            Timber.d("First recycler item is header, offset=$topOffset")
                            debug_view.layoutParams = ConstraintLayout.LayoutParams(100, topOffset)
                            background_slide.setOverlayOffset(topOffset)
                        }
                    }
                })

                initializeImage()

            }

            override fun onFailure(call: Call<Feed>?, t: Throwable?) {
                Timber.e(t)
            }

        })
//        Glide.with(this@MainActivity).load(imageUrl).into(imageView)


        Timber.d(feedJson)
    }

    private fun initializeImage() {
        val inBitmap = BitmapFactory.decodeResource(resources, R.drawable.grass)
        val outBitmap = inBitmap.copy(inBitmap.config, true)

        //Create the RenderScript context
        val rs = RenderScript.create(this)

        //Create allocations for input and output data
        val input = Allocation.createFromBitmap(rs, inBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT)
        val output = Allocation.createTyped(rs, input.getType())

        //Run a blur at the maximum supported radius (25f)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setRadius(25f)
        script.setInput(input)
        script.forEach(output)
        script.setInput(output)
        script.forEach(output)
        output.copyTo(outBitmap)

        //Tear down the RenderScript context
        rs.destroy()

        //Apply the two copies to our custom ImageView for sliding
        background_slide.setImagePair(inBitmap, outBitmap)
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
