package com.sultonbek1547.chatapprealtime.models

import java.io.Serializable

class User: Serializable {
    var uid:String?=null
    var name:String?=null
    var imageLink:String?=null
    var email:String?=null
    var statusTime:String?=null
    var isOnline:String?=null

    constructor(
        uid: String?,
        name: String?,
        imageLink: String?,
        email: String?,
        statusTime: String?,
        isOnline: String?,
    ) {
        this.uid = uid
        this.name = name
        this.imageLink = imageLink
        this.email = email
        this.statusTime = statusTime
        this.isOnline = isOnline
    }

    constructor()
}