package com.github.sergey.bibikov.georgian

import android.os.Environment
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.createDirectory

val esd = Environment.getExternalStorageDirectory()
val ROOT_DIR = "$esd/Documents/Georgian"

fun getRelativeDirs(dirNames: List<String>): List<String> {
    return dirNames.map { "$ROOT_DIR/$it" }
}

fun createDirs(dirsToCreate: List<String>) {
    dirsToCreate.forEach {
        Paths.get(it).toAbsolutePath().createDirectory()
    }
}

fun createFile(input: InputStream?, fileName: String) {
    val fName = Paths.get(fileName).toAbsolutePath()
    input.use {
        Files.copy(it, fName)
    }
}