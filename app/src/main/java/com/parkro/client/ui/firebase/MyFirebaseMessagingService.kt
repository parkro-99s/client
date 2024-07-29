package com.parkro.client.ui.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Override
    override fun onNewToken(token: String) {
        // 해당 기기의 FCM 토큰이 변경되었을 때의 작업
        super.onNewToken(token)
        Log.d("MyFirebaseMessagingService", "FCM token: $token")
    }

    @Override
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 푸시 메시지 수신 시 작업
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default_channel_id"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
//            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }

//    override fun onMessageReceived(@NonNull remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//        Log.d(TAG, "From: " + remoteMessage.from)
//        if (remoteMessage.notification != null) {
//            sendNotification(
//                remoteMessage.notification!!.title, remoteMessage.notification!!
//                    .body
//            )
//        }
//    }
}