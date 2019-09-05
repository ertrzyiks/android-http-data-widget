package com.example.httpdatawidget

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Response

class ResponseGrader(
    protected val response: Response
) {
   fun isStatusCodeOk() : Boolean {
       return response.code in 200..299
   }

  fun isContentTypeOk(): Boolean {
    return contentType() == "application/json"
  }

  fun isContentOk(): Boolean {
      try {
        val mapperAll = ObjectMapper()
        mapperAll.readTree(response.body!!.string())
        return true
      }
      catch(e: Exception) {
      }

      return false
  }

  fun statusCode(): String {
      return response.code.toString()
  }

  fun contentType(): String {
      var contentTypeHeader = response.header("Content-type")

      if (contentTypeHeader === null) {
          return "Unknown"
      }

      return contentTypeHeader.split(";")[0]
  }

  fun contentSample(): String {
      val content = response.body!!.string()

      if (content == "") {
          return "(empty)"
      }

      return content
  }
}