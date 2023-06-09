package com.sultonbek1547.chatapprealtime.utils

import com.google.firebase.database.DatabaseReference
import com.sultonbek1547.chatapprealtime.models.Group
import com.sultonbek1547.chatapprealtime.models.User

object MyData {
    var USER = User()
    var screenLengthItem = 1
    var chatReference: DatabaseReference? = null
    var userList = ArrayList<User>()
    var GROUP = Group()

    var TYPE_TEXT = "text"
    var TYPE_IMAGE = "image"


    fun getSenderUser(id:String):User{
        userList.forEach {
            if (id == it.uid) return it
        }
        return USER
    }
}