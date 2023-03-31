package com.utils



/**
 * @author Arabin
 * @date 03/20/2023
 * A builder that builds the main
 * command to perform analysis
 * designed with builder pattern
 * */
class CommandBuilder private constructor(
    private val analyzer: String?,
    private val androidJar: String?,
    private val debugApkPath: String?,
    private val sourceSinkPath: String?
){

    class Builder(private val basePath: String?) {
        private var analyzer: String? = null
        private var androidJar: String? = null

        fun setAnalyzerPath(analyzerPath: String) = apply { this.analyzer = analyzerPath }
        fun setAndroidJarPath(androidJarPath: String) = apply { this.androidJar = androidJarPath }

        fun build() = CommandBuilder(analyzer, androidJar, basePath+AppMacros.APK_PATH,
            basePath+"/"+AppMacros.SOURCE_SINKS)

    }

    fun createAnalysisCommand(): String{
        return "java -jar $analyzer -a $debugApkPath -p $androidJar -s $sourceSinkPath"
    }

}