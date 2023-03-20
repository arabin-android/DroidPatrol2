package com.utils

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.*
import java.util.concurrent.Executors

class BuildApk : AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val isWindows = System.getProperty("os.name")
            .lowercase(Locale.getDefault()).startsWith("windows")
        println(if (isWindows) "This is a Windows machine." else "This is NOT a windows machine.")
        val currentProject = anActionEvent.project
        buildApk(currentProject)
    }

    val jarFilePath: Unit
        get() {
            try {
                val path = BuildApk::class.java.protectionDomain.codeSource.location.path
                val decodedPath = URLDecoder.decode(path, "UTF-8")
                println(decodedPath)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }
    val listDir: Unit
        get() {
            val homeDirectory = System.getProperty("user.home")
            var process: Process? = null
            if (com.intellij.rt.execution.testFrameworks.ProcessBuilder.isWindows) {
                try {
                    process = Runtime.getRuntime()
                        .exec(String.format("cmd.exe /c dir %s", homeDirectory))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    process = Runtime.getRuntime()
                        .exec(String.format("sh -c ls %s", homeDirectory))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val streamGobbler = StreamGobblerLocal(process!!.inputStream) { x: String? -> println(x) }
            Executors.newSingleThreadExecutor().submit(streamGobbler)
            var exitCode = 0
            try {
                exitCode = process.waitFor()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            assert(exitCode == 0)
        }
    val listDirWithCustom: Unit
        get() {
            val builder = ProcessBuilder()
            if (com.intellij.rt.execution.testFrameworks.ProcessBuilder.isWindows) {
                builder.command("cmd.exe", "/c", "adb devices")
            } else {
                builder.command("sh", "-c", "ls")
            }
            builder.redirectErrorStream(true)
            builder.directory(File(System.getProperty("user.home")))
            var process: Process? = null
            try {
                process = builder.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val streamGobbler = StreamGobblerLocal(process!!.inputStream) { x: String? -> println(x) }
            Executors.newSingleThreadExecutor().submit(streamGobbler)
            var exitCode = 0
            try {
                exitCode = process.waitFor()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            assert(exitCode == 0)
        }

    fun buildApk(project: Project?) {
        println("Current working directory : " + project!!.basePath)
        try {
            Runtime.getRuntime()
                .exec("cmd.exe /k cd \"" + project.basePath + "\" & start cmd.exe /c \"gradlew assembleDebug\"")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showNotificationBallon(title: String, message: String) {
        val NOTIFICATION_GROUP: NotNullLazyValue<NotificationGroup> = object : NotNullLazyValue<NotificationGroup>() {
            override fun compute(): NotificationGroup {
                return NotificationGroup(
                    "Analysis Started > ",
                    NotificationDisplayType.STICKY_BALLOON,
                    true
                )
            }
        }
        ApplicationManager.getApplication()
            .invokeLater(
                {
                    Notifications.Bus.notify(
                        NOTIFICATION_GROUP.value
                            .createNotification(
                                title,
                                message,
                                NotificationType.ERROR,
                                null
                            )
                    )
                },
                ModalityState.NON_MODAL
            )
    }
}