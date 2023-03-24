package com.utils


/**
 * @author Arabin
 * @date 03/20/2023
 * All constants are placed here
 * */
class AppMacros {

    companion object{
        const val DRIVE_LOCATION = "Please Enter the Drive name for analyzer.jar and android.jar !!!"
        const val DRIVE_TITLE = "Library path"
        const val ANALYZER = "analyzer.jar"
        const val SOURCE_SINKS = "SourcesAndSinks.txt"
        const val SOURCE_NAME = "Please Enter the Source name: "
        const val SOURCE_TITLE = "Sources"
        const val SINK_NAME = "Please Enter the Sink name: "
        const val SINK_TITLE = "Sinks"
        const val APK_PATH = "/app/build/outputs/apk/debug/app-debug.apk"
        const val APK_FILE = "app-debug.apk"
        const val ANDROID_JAR = "android.jar"
        const val NO_APK_ERROR ="Please build the apk first and try again !!!"
        const val ERROR_TITLE = "Apk doesn't exists"
        const val ANALYSIS_ON_PROGRESS = "Analysis in on progress please wait !!!"
        const val ANALYSIS_PROGRESS_TITLE = "Processing !!"
        const val DEBUG_APK_DIRECTORY = "/app/build/outputs/apk/"
        const val NOT_DIRECTORY = "Passed folder not exists!!"
        const val DIRECTORY_TITLE = "No such directory"
        const val PROCESS_CALLED_WTH = "was called with values from the following sources:"
        const val ANALYSIS_REPORT = "Analysis report"
        const val TAG_LEAKS = "leaks"
        const val SOURCE_SINK_MISSING = "No source sinks available"
        const val NO_SOURCE_SINK_TITLE = "No source sinks"
    }

}