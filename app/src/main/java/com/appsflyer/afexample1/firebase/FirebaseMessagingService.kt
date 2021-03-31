package com.appsflyer.afexample1.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import com.appsflyer.AppsFlyerLib
import com.appsflyer.afexample1.R
import com.appsflyer.afexample1.activities.MainActivity
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.MSG_BODY
import com.appsflyer.afexample1.network.MyJsonKeys.Companion.MSG_TITLE
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "AppsFlyer_API_Demo"
        private const val REQUEST_CODE = 2918
        private const val NOTIFICATION_CODE = 1829
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        //https://support.appsflyer.com/hc/en-us/articles/210289286#android-uninstall-4-implementing-appsflyer-uninstall-measurement-service
        AppsFlyerLib.getInstance().updateServerUninstallToken(applicationContext, p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        if (p0.data.isNotEmpty() && !p0.data.containsKey(getString(R.string.afSilentPush))) {
            Log.d(TAG, "onMessageReceived: ${p0.data}")
            sendNotification(p0.data)
        }
    }


    private fun sendNotification(@NonNull messageData: Map<String, String>) {
        Log.d(TAG, "sendNotification: ")
        val bundle = Bundle()
        for ((k, v) in messageData) {
            bundle.putString(k, v)
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.data = Uri.parse(getString(R.string.pushDeepLinkUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtras(bundle)
        val pendingIntent = PendingIntent.getActivity(
            this, REQUEST_CODE, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.af)
            .setContentTitle(if (messageData[MSG_TITLE].isNullOrEmpty()) getString(R.string.fcm_message) else messageData[MSG_TITLE])
            .setContentText(if (messageData[MSG_BODY].isNullOrEmpty()) getString(R.string.fcm_message_content) else messageData[MSG_BODY])
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.default_notification_channel_id),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_CODE, notificationBuilder.build())
    }
}
