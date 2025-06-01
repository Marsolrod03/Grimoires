package com.grimoires.Grimoires.data.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

object NotificationService {

    private const val FCM_URL = "https://fcm.googleapis.com/fcm/send"
    private const val SERVER_KEY = "AIzaSyDVadAMD2l76cAAY-kmbzk4XQfVMyPEsdY"

    private val client = OkHttpClient()

    fun sendMulticastNotification(tokens: List<String>, title: String, message: String) {
        val json = JSONObject().apply {
            put("registration_ids", tokens)
            put("priority", "high")

            put("notification", JSONObject().apply {
                put("title", title)
                put("body", message)
                put("sound", "default")
            })
        }

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url(FCM_URL)
            .addHeader("Authorization", "key=$SERVER_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error enviando notificación: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                println("Notificación enviada: ${response.body?.string()}")
            }
        })
    }
}
