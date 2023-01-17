package com.example.bakeryapp.util

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object AdminRepository {


     fun addItem(itemData: ItemData) {
        val url = URL("http://192.168.56.1:3000/addItem")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.connect()
        val json = JSONObject().apply {
            put("itemId", itemData.itemId)
            put("name", itemData.name)
            put("image", itemData.image)
            put("description", itemData.description)
            put("limit", itemData.limit)
            put("cost", itemData.cost)
            put("currency", itemData.currency)
        }
        val wr = OutputStreamWriter(connection.outputStream)
        wr.write(json.toString())
        wr.flush()
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val jsonString = connection.inputStream.use { it.reader().readText() }
            val response = JSONObject(jsonString)
            if (response.getBoolean("success")) {
                println("Item added successfully")
                itemData.itemId = response.getString("itemId")
            } else {
                println("Error adding item: ${response.getString("error")}")
            }
        } else {
            println("Error adding item: HTTP error code $responseCode")
        }
    }

    fun addMaterial(materialData:MaterialsData) {
        val url = URL("http://192.168.56.1:3000/addMaterial")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.connect()
        val json = JSONObject().apply {
            put("materialId", materialData.materialId)
            put("name", materialData.name)
            put("description", materialData.description)
            put("cost", materialData.cost)
            put("currency", materialData.currency)
            put("unit", materialData.unit)
            put("contactInfoName", materialData.contactInfoName)
            put("contactInfoPhone", materialData.contactInfoPhone)
        }
        val wr = OutputStreamWriter(connection.outputStream)
        wr.write(json.toString())
        wr.flush()
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val jsonString = connection.inputStream.use { it.reader().readText() }
            val response = JSONObject(jsonString)
            if (response.getBoolean("success")) {
                println("material added successfully")
                materialData.materialId = response.getString("materialId")
            } else {
                println("Error adding item: ${response.getString("error")}")
            }
        } else {
            println("Error adding material: HTTP error code $responseCode")
        }
    }
}