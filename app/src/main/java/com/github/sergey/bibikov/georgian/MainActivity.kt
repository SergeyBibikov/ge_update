package com.github.sergey.bibikov.georgian

import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.sergey.bibikov.georgian.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
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
    Files.copy(input, fName)
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val b = findViewById<Button>(R.id.b)

        b.setOnClickListener {
            MainScope().launch {
                Runtime.getRuntime().exec("rm -rf $ROOT_DIR").waitFor()
                val settings = parseDownloadSettings()
                val dirs = getRelativeDirs(settings.dirs)
                createDirs(listOf(ROOT_DIR).plus(dirs))
                for (f in settings.files) {
                    download(f)
                }
                finishAndRemoveTask()
            }
        }
    }
}

fun download(file: String) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://raw.githubusercontent.com/SergeyBibikov/ge-html/main/$file.html")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            TODO("Not yet implemented")
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                response.body?.byteStream().apply {
                    createFile(this, "$ROOT_DIR/$file.html")
                }
            }
        }
    })
}