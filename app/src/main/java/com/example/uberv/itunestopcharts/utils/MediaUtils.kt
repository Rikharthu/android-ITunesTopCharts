package com.example.uberv.itunestopcharts.utils

import android.content.Context
import android.graphics.Bitmap
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.Element
import android.support.v8.renderscript.RenderScript
import android.support.v8.renderscript.ScriptIntrinsicBlur

fun createBlurryBackground(image: Bitmap, width: Int, height: Int, context: Context): Bitmap {
    val imageWidth = image.width
    val imageHeight = image.height
    val aspectRatio = imageWidth / imageHeight

    val blurry = Bitmap.createScaledBitmap(image, imageWidth / 2, imageHeight / 2, false)
    val blurRadius = 2f
    val rs = RenderScript.create(context)
    val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val input = Allocation.createFromBitmap(rs, blurry)
    val output = Allocation.createFromBitmap(rs, blurry)
    blurScript.setRadius(blurRadius)
    blurScript.setInput(input)
    blurScript.forEach(output)
    output.copyTo(blurry)

    return Bitmap.createScaledBitmap(blurry, height*aspectRatio, height , true)
}