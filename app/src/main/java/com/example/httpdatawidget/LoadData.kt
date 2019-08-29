package com.example.httpdatawidget

import android.content.Context
import android.os.AsyncTask
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class LoadData() : AsyncTask<Void, Void, String>() {
    internal var callback: LoadDataCallback<String> by Delegates.notNull()
    internal var context: Context by Delegates.notNull()

    constructor(context: Context, callback: LoadDataCallback<String>) : this() {
        this.callback = callback
        this.context = context
    }

    override fun doInBackground(vararg params: Void?): String? {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()

        val url = URL("https://papi.ertrzyiks.me/food-time?access_token=123&query=%7BlastEntryDate%28spaceId%3A%20%22f54dc7be-e6ba-4fbe-ac39-935fae3f23c2%22%29%7D")
//        val url = URL("https://1f95c3a3.ngrok.io/food-time?access_token=123&query=%7BlastEntryDate%28spaceId%3A%226232f3b3-d2e7-4f4e-b3fd-998d6d4eb3ae%22%29%7D")

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body!!.string()

            val mapperAll = ObjectMapper()
            val objData = mapperAll.readTree(responseBody)

            return getText(objData, "data.lastEntryDate")
        } catch (e: Exception) {
            return e.message
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        if (result !== null) {
            this.callback.onSuccess(result)
        }
    }

    fun getText(objData: JsonNode, path: String): String {
        var currentNode = objData

        path.split('.').forEach {
            currentNode = currentNode.path(it)
        }

        return currentNode.asText("Failed to read " + path)
    }
}