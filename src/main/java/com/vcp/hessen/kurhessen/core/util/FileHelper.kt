package com.vcp.hessen.kurhessen.core.util

import java.io.File
import java.text.DecimalFormat

object FileHelper {


    public fun getSizeOfFileString(path: String) : String {
        val file = File(path)
        if (file.exists()) {
            // File size in bytes
            val bytes: Long = file.length()
            val kilobytes = bytes.toDouble() / 1024
            val megabytes = kilobytes / 1024
            val gigabytes = megabytes / 1024

            val df = DecimalFormat("#.#")

            if (gigabytes > 1) {
                return "${df.format(gigabytes)} GB"
            }
            if (megabytes > 1) {
                return "${df.format(megabytes)} MB"
            }
            if (kilobytes > 1) {
                return "${df.format(kilobytes)} KB"
            }
            return "$bytes B"
        } else {
            return "File not found."
        }
    }
    public fun getSizeOfFileString(bytes: Long) : String {
        // File size in bytes
        val kilobytes = bytes.toDouble() / 1024
        val megabytes = kilobytes / 1024
        val gigabytes = megabytes / 1024

        val df = DecimalFormat("#.#")

        if (gigabytes > 1) {
            return "${df.format(gigabytes)} GB"
        }
        if (megabytes > 1) {
            return "${df.format(megabytes)} MB"
        }
        if (kilobytes > 1) {
            return "${df.format(kilobytes)} KB"
        }
        return "$bytes B"
    }
}