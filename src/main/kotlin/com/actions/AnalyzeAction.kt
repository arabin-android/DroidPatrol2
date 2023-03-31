package com.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.utils.AppMacros
import com.utils.CommandBuilder
import com.utils.FileCheckHelper
import com.utils.Helper
import java.io.*
import java.util.*


/**
 * @author Arabin
 * @date 03/20/2023
 * An action that performs data flow analysis
 * uses soot and jimple for analyze kotlin code
 * */
class AnalyzeAction : DumbAwareAction("Analyze"), FileCheckHelper.IPassChecks {

    private var mProject: Project? = null
    private var fileCheckHelper: FileCheckHelper? = null

    override fun actionPerformed(e: AnActionEvent) {
        mProject = e.project
        val location = Messages.showInputDialog(
            e.getData(PlatformDataKeys.PROJECT), AppMacros.DRIVE_LOCATION, AppMacros.DRIVE_TITLE, Messages.getQuestionIcon()
        )
        fileCheckHelper = FileCheckHelper(this, e.project?.basePath, location)
        fileCheckHelper?.checkApk()
    }

    private fun analyzeApk(location: String) {
        val helper = Helper(location, mProject?.basePath)
        val commandBuilder = CommandBuilder.Builder(mProject?.basePath)
            .setAnalyzerPath(helper.getSootPath())
            .setAndroidJarPath(helper.getAndroidJar())
            .build()

        val builder = helper.getProcess(commandBuilder.createAnalysisCommand())
            .directory(mProject?.basePath?.let { File(it) })
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
        showAlertDialog(AppMacros.ANALYSIS_ON_PROGRESS, AppMacros.ANALYSIS_PROGRESS_TITLE)
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
            showAlertDialog(text.toString(), AppMacros.ANALYSIS_REPORT)
        } else {
            showAlertDialog(leaksText, AppMacros.ANALYSIS_REPORT)
        }
    }

    override fun onSuccess(aCheckType: FileCheckHelper.CHECK_TYPE) {
        when(aCheckType){
            FileCheckHelper.CHECK_TYPE.IS_SOURCE_SINK_AVAILABLE->{
                fileCheckHelper?.getLocation()?.let { analyzeApk(it) }
            }

            FileCheckHelper.CHECK_TYPE.IS_VALID_APK->{
                fileCheckHelper?.checkDirectory()
            }

            FileCheckHelper.CHECK_TYPE.IS_DIRECTORY->{
                fileCheckHelper?.checkSourceSink()
            }
        }
    }

    override fun onFailed(aCheckType: FileCheckHelper.CHECK_TYPE, message: String) {
        var errorTitle : String? = null
        errorTitle = when(aCheckType){
            FileCheckHelper.CHECK_TYPE.IS_SOURCE_SINK_AVAILABLE->{
                AppMacros.NO_SOURCE_SINK_TITLE
            }

            FileCheckHelper.CHECK_TYPE.IS_VALID_APK->{
                AppMacros.ERROR_TITLE
            }

            FileCheckHelper.CHECK_TYPE.IS_DIRECTORY->{
                AppMacros.DIRECTORY_TITLE
            }
        }
        showAlertDialog(message, errorTitle)
    }

    private fun showAlertDialog(message: String, title: String){
        Messages.showMessageDialog(
            mProject, message, title, Messages.getInformationIcon()
        )
    }

}