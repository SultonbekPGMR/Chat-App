package com.sultonbek1547.chatapprealtime.retrofit

import com.sultonbek1547.chatapprealtime.models.notification.MyResponse
import com.sultonbek1547.chatapprealtime.models.notification.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers(
        "Content-type:application/json",
        "Authorization:key=AAAAagIc0YE:APA91bHXWWgQu_W5lPjx3QPleZGnM29OfVVwvbdzw8BbF1ZpBxQBKBD7okElkahFmHuqliS0jLhB5lim-JZanANN21QTRsNcG0hhkFU2xbHZHEAyAXV0QGmycK_JOSkcCXYAGUlm0oCk"
    )
    @POST("fcm/send")
    fun sendNotification(@Body sender: Sender): Call<MyResponse>
}