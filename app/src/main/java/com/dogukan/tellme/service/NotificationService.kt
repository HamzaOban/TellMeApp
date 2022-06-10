package com.dogukan.tellme.service
import com.dogukan.tellme.constants.AppConstants.Companion.CONTENT_TYPE
import com.dogukan.tellme.constants.AppConstants.Companion.SERVER_KEY
import com.dogukan.tellme.models.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationService {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotifitication(
            @Body notification : PushNotification
    ) : Response<ResponseBody>

}