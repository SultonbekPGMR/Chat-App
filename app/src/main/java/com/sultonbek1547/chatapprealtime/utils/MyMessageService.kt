package com.sultonbek1547.chatapprealtime.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.utils.MyData.USER

private const val TAG = "GGGG"

class MyMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "GGGG: $token")
    }

    var channelId = 1

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "GGGG: Notification keldi")

        if (message.data.isNotEmpty()) {
            if (message.data["user"] == USER.uid) {
                val builder = NotificationCompat.Builder(this, channelId.toString())
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(message.data["title"])
                    .setContentTitle(message.data["sented"])
                    .setContentText(message.data["body"])
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)

                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = getString(R.string.app_name)
                    val descriptionText = getString(R.string.app_name)
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel("$channelId", name, importance).apply {
                        description = descriptionText
                    }
                    // Register the channel with the system
                    notificationManager.createNotificationChannel(channel)
                }


                notificationManager.notify(channelId++, builder.build())
            }
        }else{
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
        }


    }

}