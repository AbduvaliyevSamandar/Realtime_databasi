package com.example.realtimedatabasi.models

class Message {

    var text:String?=null
    var toUserUid:String?=null
    var fromUserUid:String?=null
    var date:String?=null

    constructor(text: String?, toUserUid: String?, fromUserUid: String?, date: String?) {
        this.text = text
        this.toUserUid = toUserUid
        this.fromUserUid = fromUserUid
        this.date = date
    }

    constructor()


}