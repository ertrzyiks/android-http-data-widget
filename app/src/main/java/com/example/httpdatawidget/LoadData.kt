package com.example.httpdatawidget

import android.content.Context
import android.os.AsyncTask
import com.example.httpdatawidget.storage.DatasourceInfo
import okhttp3.*
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

//val responseBody = response.body!!.string()

class LoadData() : AsyncTask<DatasourceInfo, Void, Response>() {
    internal var callback: LoadDataCallback<Response> by Delegates.notNull()
    internal var context: Context by Delegates.notNull()
    internal var exception: Exception? = null

    constructor(context: Context, callback: LoadDataCallback<Response>) : this() {
        this.callback = callback
        this.context = context
    }

    override fun doInBackground(vararg params: DatasourceInfo): Response? {
        val client = OkHttpClient.Builder()
            .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .followSslRedirects(true)
            .build()

        val info = params.get(0)

        try {
            val url = URL(info.url)

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            return client.newCall(request).execute()
        } catch (e: Exception) {
            exception = e
        }

        return null
    }

    override fun onPostExecute(result: Response?) {
        super.onPostExecute(result)

        if (result !== null) {
            this.callback.onSuccess(result)
        } else {
            this.callback.onFailure(exception!!)
        }
    }
}