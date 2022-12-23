package com.github.sergey.bibikov.georgian

import okhttp3.ResponseBody
import okio.internal.commonToUtf8String
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.InputStream

interface Some {

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String): ResponseBody

}

suspend fun downloadFile(url: String): InputStream {
    return Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/SergeyBibikov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Some::class.java)
        .downloadFile(url)
        .byteStream()
}

data class Settings(val dirs: List<String>, val files: List<String>)

suspend fun parseDownloadSettings(): Settings {
    val dirs = mutableListOf<String>()
    val files = mutableListOf<String>()
    val str = downloadFile("ge_update/main/downloadSettings.json")
    val obj: JSONObject
    str.use {
        obj = JSONObject(str.readBytes().commonToUtf8String())
    }
    obj.getJSONArray("directories").let {
        0.until(it.length()).forEach { i -> dirs.add(it.getString(i)) }
    }
    obj.getJSONArray("files").let {
        0.until(it.length()).forEach { i -> files.add(it.getString(i)) }
    }

    return Settings(dirs, files)
}