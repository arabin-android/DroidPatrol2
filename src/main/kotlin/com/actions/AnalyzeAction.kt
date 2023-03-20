package com.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.utils.AppMacros
import com.utils.CommandBuilder
import com.utils.Helper
import java.io.*
import java.util.*


/**
 * @author Arabin
 * @date 03/20/2023
 * An action that performs data flow analysis
 * uses soot and jimple for analyze kotlin code
 * */
class AnalyzeAction : DumbAwareAction("Analyze") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(PlatformDataKeys.PROJECT)
        val location = Messages.showInputDialog(
            project, AppMacros.DRIVE_LOCATION, AppMacros.DRIVE_TITLE, Messages.getQuestionIcon()
        )
        val helper = Helper(location, e.project?.basePath)
        if (!helper.isDirectory()){
            project?.let { helper.showAlertDialog(it, AppMacros.NOT_DIRECTORY, AppMacros.DIRECTORY_TITLE) }
            return
        }
        if (helper.getApk() == null) {
            project?.let { helper.showAlertDialog(it, AppMacros.NO_APK_ERROR, AppMacros.ERROR_TITLE) }
            return
        }
        location?.let { analysisApk(e.project, it) }
    }

    private fun analysisApk(project: Project?, location: String) {
        val helper = Helper(location, project?.basePath)
        val commandBuilder = CommandBuilder.Builder()
            .setAnalyzerPath(helper.getSootPath())
            .setAndroidJarPath(helper.getAndroidJar())
            .setBasePath(project?.basePath)
            .build()

        val builder = helper.getProcess(commandBuilder.createCommand())
            .directory(project?.basePath?.let { File(it) })
        builder.redirectErrorStream(true)
        var p: Process? = null
        try {
            p = builder.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val r = p?.inputStream?.let { InputStreamReader(it) }?.let { BufferedReader(it) }
        var line: String?
        var leaksText = ""
        val text = StringBuilder()
        project?.let { helper.showAlertDialog(it, AppMacros.ANALYSIS_ON_PROGRESS, AppMacros.ANALYSIS_PROGRESS_TITLE) }
        while (true) {
            try {
                line = r?.readLine()
                if (line == null) {
                    break
                }
                text.append(line)
                text.append("\n")
                if (line.contains(AppMacros.TAG_LEAKS)) leaksText = line
                println(line)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val sb = StringBuilder()
        val sc = Scanner(text.toString())
        var source: String? = null
        while (sc.hasNextLine()) {
            source = sc.findInLine(AppMacros.PROCESS_CALLED_WTH)
            if (source != null) break
            sc.nextLine()
        }
        if (source != null) {
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine())
                sb.append("\n")
            }
            Messages.showMessageDialog(project, text.toString(), AppMacros.ANALYSIS_REPORT, Messages.getInformationIcon())
        } else {
            Messages.showMessageDialog(project, leaksText, AppMacros.ANALYSIS_REPORT, Messages.getInformationIcon())
        }
    }

}