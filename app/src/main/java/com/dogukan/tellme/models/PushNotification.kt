package com.dogukan.tellme.models

data class PushNotification(
    val data : NotificationData,
    val to : String
) {
}