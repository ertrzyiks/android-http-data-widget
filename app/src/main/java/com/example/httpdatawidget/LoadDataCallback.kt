package com.example.httpdatawidget

import java.lang.Exception

interface LoadDataCallback<T> {
    fun onSuccess(value: T)
    fun onFailure(e: Exception)
    fun onDone()
}
