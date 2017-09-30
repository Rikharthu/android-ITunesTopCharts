package com.example.uberv.itunestopcharts.utils

import timber.log.Timber

open class DevelopmentTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String {
        return "${super.createStackElementTag(element)}#${element.methodName}:${element.lineNumber}"
    }
}