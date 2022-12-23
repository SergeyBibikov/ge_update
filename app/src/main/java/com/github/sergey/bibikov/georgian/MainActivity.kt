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
                    val i = downloadFile("ge-html/main/$f.html")
                    i.use{
                        createFile(
                            i, "$ROOT_DIR/$f.html"
                        )
                    }
                }
                finishAndRemoveTask()
            }
        }
    }
}