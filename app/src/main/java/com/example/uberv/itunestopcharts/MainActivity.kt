package com.example.uberv.itunestopcharts

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.uberv.itunestopcharts.api.RestAPI
import com.example.uberv.itunestopcharts.api.models.FeedResponseMain
import com.example.uberv.itunestopcharts.data.asFeed
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val restApi = RestAPI()
        restApi.getHotTracks().enqueue(object : Callback<FeedResponseMain> {
            override fun onResponse(call: Call<FeedResponseMain>?, response: Response<FeedResponseMain>?) {
                var feed = response?.body()?.feed?.asFeed()
                Timber.d(feed.toString())
            }

            override fun onFailure(call: Call<FeedResponseMain>?, t: Throwable?) {
                TODO("not implemented")
            }

        })

        // TODO for debug
        // https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview117/v4/fe/ec/8b/feec8bd8-719c-4b3b-c9fc-b3035c307534/mzaf_6531891649588563889.plus.aac.p.m4a
        val url = "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview117/v4/fe/ec/8b/feec8bd8-719c-4b3b-c9fc-b3035c307534/mzaf_6531891649588563889.plus.aac.p.m4a"
        val player = MediaPlayer()
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player.setDataSource(url)
        player.prepare()
        val duration = player.duration
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val position = player.currentPosition
                Timber.d("$position/$duration")
                val diff = duration - position
                if (diff < 3000) {
                    val sound: Float = diff / 3000.toFloat()
                    Timber.d("volume $sound")
                    player.setVolume(sound, sound)
                }
            }
        }, 0, 50)
        timer
        player.setOnCompletionListener {
            timer.cancel()
            timer.purge()
        }
        player.start()
    }
}
