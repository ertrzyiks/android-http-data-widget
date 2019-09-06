package com.example.httpdatawidget

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Response

class ResponseGrader {
   protected var response: Response
   protected var responseBody: String

   constructor(response: Response) {
      this.response = response
      this.responseBody = response.body!!.string()
   }

   fun isStatusCodeOk() : Boolean {
       return response.code in 200..299
   }

  fun isContentTypeOk(): Boolean {
    return contentType() == "application/json"
  }

  fun isContentOk(): Boolean {
      try {
        val mapperAll = ObjectMapper()
        mapperAll.readTree(responseBody)

        return true
      }
      catch(e: Exception) {
          e.printStackTrace()
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
      if (responseBody == "") {
          return "(empty)"
      }

      return responseBody
  }
}