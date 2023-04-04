package com.sultonbek1547.chatapprealtime.models

class Message {
    var id:String? = null
    var textOrImage: String? = null
    var messageText: MessageText? = null
    var messageImage: MessageImage? = null
    var senderId:String? = null
    var receiverId:String? = null
    var date:String? = null
    var statusRead:String? = null
    var positon:String? = null



    constructor()
    constructor(
        id: String?,
        textOrImage: String?,
        messageText: MessageText?,
        messageImage: MessageImage?,
        senderId: String?,
        receiverId: String?,
        date: String?,
        statusRead: String?,
        positon: String?,
    ) {
        this.id = id
        this.textOrImage = textOrImage
        this.messageText = messageText
        this.messageImage = messageImage
        this.senderId = senderId
        this.receiverId = receiverId
        this.date = date
        this.statusRead = statusRead
        this.positon = positon
    }
}