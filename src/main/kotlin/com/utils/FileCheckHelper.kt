package com.utils

import java.io.File

class FileCheckHelper(private val aIPassChecks: IPassChecks, private val basePath: String?, private val location: String?) {

    fun isDirectory(){
        val directory = File("$location:/")
        if (directory.isDirectory){
            aIPassChecks.onSuccess(CHECK_TYPE.IS_DIRECTORY)
        }else{
            aIPassChecks.onFailed(CHECK_TYPE.IS_DIRECTORY, AppMacros.NOT_DIRECTORY)
        }
    }

    fun checkApk(){
        val apkDirectory = File(basePath+AppMacros.DEBUG_APK_DIRECTORY)
        val findFile = FindFile(AppMacros.APK_FILE, apkDirectory, 6)
        if (findFile.find() != null)
            aIPassChecks.onSuccess(CHECK_TYPE.IS_VALID_APK)
        else aIPassChecks.onFailed(CHECK_TYPE.IS_VALID_APK, AppMacros.NO_APK_ERROR)
    }

    fun checkSourceSink(){
        val sourceSinkDirectory = File(basePath)
        val findSourceSink = FindFile(AppMacros.SOURCE_SINKS, sourceSinkDirectory, 6)
        if (findSourceSink.find() != null)
            aIPassChecks.onSuccess(CHECK_TYPE.IS_SOURCE_SINK_AVAILABLE)
        else aIPassChecks.onFailed(CHECK_TYPE.IS_SOURCE_SINK_AVAILABLE, AppMacros.SOURCE_SINK_MISSING)
    }

    enum class CHECK_TYPE{
        IS_DIRECTORY,
        IS_VALID_APK,
        IS_SOURCE_SINK_AVAILABLE
    }


    interface IPassChecks{
        fun onSuccess(aCheckType: CHECK_TYPE)
        fun onFailed(aCheckType: CHECK_TYPE, message: String)
    }

}