package com.sunyuan.click.debounce.utils

import com.didiglobal.booster.kotlinx.file
import com.sunyuan.click.debounce.EXTENSION_NAME
import com.sunyuan.click.debounce.config.DebounceExtension
import org.gradle.api.Project
import java.io.File

/**
 * @author sy007
 * @date 2022/08/31
 * @description
 */

private const val DEBOUNCE_ENABLE = "debounceEnable"

fun Project.getReportFile(dirName: String, fileName: String): File {
    return project.buildDir.file("reports", "debounce-plugin", dirName, fileName)
}

val Project.enablePlugin: Boolean
    get() = if (project.hasProperty(DEBOUNCE_ENABLE)) {
        project.properties[DEBOUNCE_ENABLE].toString().toBoolean()
    } else {
        true
    }

val Project.debounceEx: DebounceExtension
    get() = extensions.findByName(EXTENSION_NAME) as DebounceExtension


