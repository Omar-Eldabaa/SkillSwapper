package com.example.skillswapper

data class Message(
    val message :String?=null,
    val posActionName:String?=null,
    val posActionClick:OnDialogActionClickListener?=null,
    val negActionName:String?=null,
    val negActionClick:OnDialogActionClickListener?=null,
    val isCancelable:Boolean=true
)
fun interface OnDialogActionClickListener{
    fun onActionClick()

}