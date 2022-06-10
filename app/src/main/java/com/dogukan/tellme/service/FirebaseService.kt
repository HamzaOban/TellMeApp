package com.dogukan.tellme.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.dogukan.tellme.R
import com.dogukan.tellme.constants.AppConstants.Companion.CHANNEL_ID
import com.dogukan.tellme.constants.AppConstants.Companion.CHANNEL_NAME
import com.dogukan.tellme.view.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,FLAG_ONE_SHOT)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationChannelCreated(notificationManager)
        }

        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()


        notificationManager.notify(notificationID,notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notificationChannelCreated(notificationManager: NotificationManager){
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH) .apply {
            description = "Title"
            enableLights(true)
            lightColor = Color.MAGENTA
        }
        notificationManager.createNotificationChannel(channel)
    }
}