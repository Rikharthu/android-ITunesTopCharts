package com.example.uberv.itunestopcharts.utils

import android.arch.lifecycle.LiveData
import com.example.uberv.itunestopcharts.api.models.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 * @param <R>
</R> */
class LiveDataCallAdapter(private val responseType: Type) : CallAdapter<Any, LiveData<ApiResponse<Any>>> {

    override fun adapt(call: Call<Any>): LiveData<ApiResponse<Any>> {
        return object : LiveData<ApiResponse<Any>>() {
            internal var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<Any> {
                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            postValue(ApiResponse(response))
                        }

                        override fun onFailure(call: Call<Any>, throwable: Throwable) {
                            postValue(ApiResponse<Any>(throwable))
                        }
                    })
                }
            }
        }
    }

    override fun responseType() = responseType
}