package com.sultonbek1547.chatapprealtime.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class Message: JsonDeserializer<Message> {
    var id:String? = null
    var senderId:String? = null
    var receiverId:String? = null
    var message:String? = null
    var date:String? = null
    var statusRead:String? = null
    var positon:String? = null

    constructor()
    constructor(
        id: String?,
        senderId: String?,
        receiverId: String?,
        message: String?,
        date: String?,
        statusRead: String?,
    ) {
        this.id = id
        this.senderId = senderId
        this.receiverId = receiverId
        this.message = message
        this.date = date
        this.statusRead = statusRead
    }

    constructor(
        id: String?,
        senderId: String?,
        receiverId: String?,
        message: String?,
        date: String?,
        statusRead: String?,
        positon: String?,
    ) {
        this.id = id
        this.senderId = senderId
        this.receiverId = receiverId
        this.message = message
        this.date = date
        this.statusRead = statusRead
        this.positon = positon
    }

    override fun toString(): String {
        return "Message(id=$id, senderId=$senderId, receiverId=$receiverId, message=$message, date=$date, statusRead=$statusRead, positon=$positon)"
    }
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Message {
        val jsonObject = json?.asJsonObject
        val idString = jsonObject?.get("id")?.asString
        val id = SimpleDateFormat("dd MM yyyy  HH mm ss SSS").format(Date())
        val senderId = jsonObject?.get("senderId")?.asString
        val receiverId = jsonObject?.get("receiverId")?.asString
        val message = jsonObject?.get("message")?.asString
        val date = jsonObject?.get("date")?.asString
        val statusRead = jsonObject?.get("statusRead")?.asBoolean
        val position = jsonObject?.get("position")?.asInt
        return Message(id, senderId, receiverId, message, date, statusRead.toString(), position.toString())
    }

}