package com.sultonbek1547.chatapprealtime.models

class Message {
    var id:String? = null
    var senderId:String? = null
    var receiverId:String? = null
    var message:String? = null
    var date:String? = null

    constructor()
    constructor(
        id: String?,
        senderId: String?,
        receiverId: String?,
        message: String?,
        date: String?,
    ) {
        this.id = id
        this.senderId = senderId
        this.receiverId = receiverId
        this.message = message
        this.date = date
    }

}