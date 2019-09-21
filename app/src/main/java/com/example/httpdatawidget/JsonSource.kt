package com.example.httpdatawidget

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

class JsonSource {
    companion object {
        fun get (responseBody: String, path: String): String {
            val mapperAll = ObjectMapper()
            val objData = mapperAll.readTree(responseBody)

            return getText(objData, path)
        }

        internal fun getText(objData: JsonNode, path: String): String {
            var currentNode = objData

            path.split('.').forEach {
                currentNode = currentNode.path(it)
            }

            return currentNode.asText("Path not found: " + path)
        }
    }
}