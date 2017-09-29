package com.example.uberv.itunestopcharts.utils

import android.content.Context
import android.util.Log
import java.io.IOException

fun readFromAssets(filename: String, context: Context): String? {
    try {
        val inputStream = context.assets.open(filename)
        // use - Executes the given block function on this resource and then closes it down correctly whether an exception is thrown or not.
        return inputStream.bufferedReader().use { it.readText() }
    } catch (e: IOException) {
        //log the exception
        Log.e("Utils", "error reading file:", e)
    }
    return null
}