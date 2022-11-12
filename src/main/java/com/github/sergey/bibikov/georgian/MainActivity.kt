package com.github.sergey.bibikov.georgian

import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.sergey.bibikov.georgian.databinding.ActivityMainBinding
import okhttp3.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths


val esd = Environment.getExternalStorageDirectory()
val geoDir = "$esd/Documents/Georgian"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val files = arrayOf("alphabet", "numbers", "phrases", "words", "grammar")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val b = findViewById<Button>(R.id.b)

        b.setOnClickListener{
            Runtime.getRuntime().exec("rm -rf $geoDir").waitFor()
            val geoAbsPath = Paths.get(geoDir).toAbsolutePath()
            Files.createDirectory(geoAbsPath)
            for ( f in files){
                download(f)
            }
            finishAndRemoveTask()
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
                response.body?.byteStream().apply{
                    val fileName = Paths.get("$geoDir/$file.html").toAbsolutePath()
                    Files.copy(this,fileName)
                }
            }
        }
    })
}