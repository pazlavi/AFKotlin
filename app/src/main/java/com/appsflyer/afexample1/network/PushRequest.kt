package com.appsflyer.afexample1.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.appsflyer.afexample1.R
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.AF
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.CAMPAIGN
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.CONTENT_AVAILABLE
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.DATA
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.DEEPLY
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.DEEP_LINK
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.MSG_BODY
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.MSG_TITLE
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.NESTED
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.PID
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.PRIORITY
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.RETARGETING
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.SOUND
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.TO
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException


class PushRequest {
    companion object {
        fun sendPushToThisDevice(context: Context, token: String) {

            val jsonBodyObj = JSONObject()
            val dataJSON = JSONObject()
            val afJSON = JSONObject()
            try {
                afJSON.put(PID, "braze_int")
                afJSON.put(RETARGETING, "true")
                afJSON.put(CAMPAIGN, "test_campaign")
                dataJSON.put(AF, afJSON)
                dataJSON.put(PRIORITY, "high")
                dataJSON.put(SOUND, "app_sound.wav")
                dataJSON.put(CONTENT_AVAILABLE, true)
                dataJSON.put(MSG_TITLE, "AppsFlyer")
                dataJSON.put(MSG_BODY, " push notifications as part of retargeting campaigns test")
                jsonBodyObj.put(TO, token)
                jsonBodyObj.put(DATA, dataJSON)
                sendPushWithBody(context, jsonBodyObj.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to create push body data", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        fun sendAddPushNotificationDeepLinkPath(context: Context, token: String) {

            val jsonBodyObj = JSONObject()
            val dataJSON = JSONObject()
            val deepLinkJSON = JSONObject()
            val nestedJSON = JSONObject()
            try {

                deepLinkJSON.put(DEEP_LINK, context.resources.getString(R.string.pushDeepLinkUrl))
                nestedJSON.put(NESTED, deepLinkJSON)
                dataJSON.put(DEEPLY, nestedJSON)
                dataJSON.put(PRIORITY, "high")
                dataJSON.put(SOUND, "app_sound.wav")
                dataJSON.put(CONTENT_AVAILABLE, true)
                dataJSON.put(MSG_TITLE, "AppsFlyer")
                dataJSON.put(MSG_BODY, "AppsFlyer push notification deep linking test")
                jsonBodyObj.put(TO, token)
                jsonBodyObj.put(DATA, dataJSON)

                sendPushWithBody(context, jsonBodyObj.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to create push body data", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        private fun sendPushWithBody(context: Context, requestBody: String) {
            Log.d("pttt", "sendPushWithBody: $requestBody")
            val queue = Volley.newRequestQueue(context)
            val url = context.resources.getString(R.string.firebaseApi)

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                url, null,
                Response.Listener { response -> Log.i("Response", response.toString()) },
                Response.ErrorListener { error ->
                    Toast.makeText(
                        context,
                        "Failed to send push: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["Authorization"] = context.resources.getString(R.string.serverKey)

                    return headers
                }

                override fun getBody(): ByteArray? {
                    return try {
                        requestBody.toByteArray(charset("utf-8"))
                    } catch (uee: UnsupportedEncodingException) {
                        VolleyLog.wtf(
                            "Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8"
                        )
                        null
                    }
                }
            }

            queue.add(jsonObjectRequest)
        }
    }

}