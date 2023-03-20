package com.utils



/**
 * @author Arabin
 * @date 03/20/2023
 * A builder that builds the main
 * command to perform analysis
 * designed with builder pattern
 * */
class CommandBuilder private constructor(
    val analyzer: String?,
    val androidJar: String?,
    val debugApkPath: String?,
    val sourceSinkPath: String?
){

    class Builder {
        private var basePath : String? = null
        private var analyzer: String? = null
        private var androidJar: String? = null

        fun setBasePath(basePath: String?) = apply { this.basePath = basePath }
        fun setAnalyzerPath(analyzerPath: String) = apply { this.analyzer = analyzerPath }
        fun setAndroidJarPath(androidJarPath: String) = apply { this.androidJar = androidJarPath }

        fun build() = CommandBuilder(analyzer, androidJar, basePath+AppMacros.APK_PATH,
            basePath+AppMacros.SOURCE_SINKS)

    }

    fun createCommand(): String{
        return "java -jar $analyzer -a $debugApkPath -p $androidJar -s $sourceSinkPath"
    }

}