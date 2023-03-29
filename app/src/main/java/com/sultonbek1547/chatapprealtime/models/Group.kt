package com.sultonbek1547.chatapprealtime.models

class Group:java.io.Serializable {
    var id:String? = null
    var groupUsersIds:String? = null
    var name:String? = null
    var listMessages:String? = null
    var groupMemberCount:String? = null

    constructor()
    constructor(
        id: String?,
        groupUsersIds: String?,
        name: String?,
        listMessages: String?,
        groupMemberCount: String?,
    ) {
        this.id = id
        this.groupUsersIds = groupUsersIds
        this.name = name
        this.listMessages = listMessages
        this.groupMemberCount = groupMemberCount
    }

}