package com.sunyuan.click.debounce

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.sunyuan.click.debounce.config.DebounceExtension
import com.sunyuan.click.debounce.task.ModifyClassesTask
import com.sunyuan.click.debounce.utils.HtmlReportUtil
import com.sunyuan.click.debounce.utils.LogUtil
import com.sunyuan.click.debounce.utils.debounceEx
import com.sunyuan.click.debounce.utils.enablePlugin
import com.sunyuan.click.debounce.utils.getReportFile
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 * author : Sy007
 * date   : 2020/11/28
 * desc   : Plugin入口
 * version: 1.0
 */
internal const val EXTENSION_NAME = "debounce"

class DebouncePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.findByName("android")
            ?: throw GradleException("$project is not an Android project")

        LogUtil.init(project.logger)

        val debounceEx = project.extensions.create(
            EXTENSION_NAME,
            DebounceExtension::class.java,
            project.objects
        )

        if (!project.enablePlugin) {
            LogUtil.warn("debounce-plugin is off.")
            return
        }

        LogUtil.warn("debounce-plugin is on.")

        val androidComponents =
            project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            val taskProvider = project.tasks.register(
                "${variant.name}DebounceModifyClasses",
                ModifyClassesTask::class.java
            ) { task ->
                task.bootClasspath.set(androidComponents.sdkComponents.bootClasspath)
                task.doLast {
                    if (debounceEx.generateReport.get()) {
                        dump(project, variant.name)
                    }
                }
            }
            variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                .use(taskProvider)
                .toTransform(
                    com.android.build.api.artifact.ScopedArtifact.CLASSES,
                    ModifyClassesTask::allJars,
                    ModifyClassesTask::allDirectories,
                    ModifyClassesTask::output
                )
        }
        project.afterEvaluate {
            project.debounceEx.init()
        }
    }

    private fun dump(project: Project, dirName: String) {
        val file = project.getReportFile(
            dirName, "modified-method-list.html"
        )
        HtmlReportUtil().dump(file)
    }
}




