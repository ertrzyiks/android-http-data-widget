package com.example.httpdatawidget

data class Response (
  var contentType: String,
  var contentBody: String,
  var statusCode: Int
){
}