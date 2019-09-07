package com.example.httpdatawidget

import com.fasterxml.jackson.databind.ObjectMapper

class ResponseGrader {
   protected var response: Response

   constructor(response: Response) {
      this.response = response
   }

   fun isStatusCodeOk() : Boolean {
       return response.statusCode in 200..299
   }

  fun isContentTypeOk(): Boolean {
    return response.contentType == "application/json"
  }

  fun isContentOk(): Boolean {
      try {
        val mapperAll = ObjectMapper()
        mapperAll.readTree(response.contentBody)

        return true
      }
      catch(e: Exception) {
          e.printStackTrace()
      }

      return false
  }

  fun contentSample(): String {
      if (response.contentBody == "") {
          return "(empty)"
      }

      return response.contentBody
  }
}