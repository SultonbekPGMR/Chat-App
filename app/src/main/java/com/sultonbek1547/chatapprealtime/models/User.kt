package com.sultonbek1547.chatapprealtime.models

class User:java.io.Serializable {
    var uid:String?=null
    var name:String?=null
    var imageLink:String?=null
    var email:String?=null

    constructor()
    constructor(uid: String?, name: String?, imageLink: String?, email: String?) {
        this.uid = uid
        this.name = name
        this.imageLink = imageLink
        this.email = email
    }


}