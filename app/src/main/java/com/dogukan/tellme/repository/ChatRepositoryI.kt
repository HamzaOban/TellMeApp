package com.dogukan.tellme.repository

import com.dogukan.tellme.models.ChatMessage

interface ChatRepositoryI {
    fun showListOfMessage(messageList : ArrayList<ChatMessage>)
    fun checkActiveState(activeState : String)
    fun checkIsSeen(isSeen : Boolean)
    fun checkIsTyping(isTyping : String)
    fun sendMessage()
    fun deleteMessage(isDeleted : Boolean)
    fun sendImage(isUpload : Boolean)
}