package com.example.uberv.itunestopcharts.utils

import android.util.Log
import com.crashlytics.android.Crashlytics

class CrashlyticsTree : DevelopmentTree() {
    private val CRASHLYTICS_KEY_PRIORITY = "priority"
    private val CRASHLYTICS_KEY_TAG = "tag"
    private val CRASHLYTICS_KEY_MESSAGE = "message"

    override fun log(priority: Int, tag: String?, message: String?, throwable: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }

        Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority)
        Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag)
        Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message)

        if (throwable == null) {
            Crashlytics.logException(Exception(message))
        } else {
            Crashlytics.logException(throwable)
        }
    }
}