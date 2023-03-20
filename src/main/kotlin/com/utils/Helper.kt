package com.utils

import com.intellij.openapi.ui.Messages
import java.io.File
import java.io.FileWriter
import java.io.IOException

import com.intellij.openapi.project.Project


/**
 * @author Arabin
 * @date 03/20/2023
 * A helper utility class
 * used to avoide boiler plate code
 * */
class Helper(private val location: String?, private val basePath: String?) {

    fun writeSourceSinks(project: Project){
        val file = File(basePath + AppMacros.SOURCE_SINKS)
        try {
            if (file.createNewFile()) {
                val writer = FileWriter(file)
                val source = Messages.showInputDialog(
                    project,
                    AppMacros.SOURCE_NAME,
                    AppMacros.SOURCE_TITLE,
                    Messages.getQuestionIcon()
                )
                writer.write(source)
                writer.write("\r\n")
                val sink = Messages.showInputDialog(
                    project,
                    AppMacros.SINK_NAME,
                    AppMacros.SINK_TITLE,
                    Messages.getQuestionIcon()
                )
                writer.write(sink)
                writer.close()
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    fun isDirectory(): Boolean{
        val directory = File("$location:/")
        return directory.isDirectory
    }

    fun getSootPath(): String{
        val baseDirectory = File("$location:/")
        val findFile = FindFile(AppMacros.ANALYZER, baseDirectory, 6)
        val analyserFile = findFile.find()
        return analyserFile.toString()
    }

    fun getApk(): File?{
        val apkDirectory = File(basePath+AppMacros.DEBUG_APK_DIRECTORY)
        val findFile = FindFile(AppMacros.APK_FILE, apkDirectory, 6)
        return findFile.find()
    }

    fun getAndroidJar(): String{
        val baseDirectory = File("$location:/")
        val findFile = FindFile(AppMacros.ANDROID_JAR, baseDirectory, 6)
        return findFile.find().toString()
    }

    fun showAlertDialog(project: Project, message: String, title: String){
        Messages.showMessageDialog(
            project, message, title, Messages.getInformationIcon()
        )
    }

    fun getProcess(command: String): ProcessBuilder{
        return ProcessBuilder(
            "cmd.exe", "/c", "cd \"C:\\Program Files\" && $command"
        )
    }

}