package com.example.httpdatawidget

import okhttp3.Response

class ResponseGrader(
    protected val response: Response
) {
   fun isStatusCodeOk() : Boolean {
       return response.code in 200..299
   }

  fun statusCode(): String {
      return response.code.toString()
  }

  fun contentType(): String {
      var contentTypeHeader = response.header("Content-type")

      if (contentTypeHeader === null) {
          return "Unknown"
      }

      return contentTypeHeader
  }

  fun contentSample(): String {
      return response.body!!.string()
  }
}