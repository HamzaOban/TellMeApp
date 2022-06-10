package com.dogukan.tellme.repository

import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import java.lang.reflect.GenericArrayType

interface LatestRepositoryI {
    fun showLatestMessage(message : ArrayList<ChatMessage>)
    fun showUserInfoLatestMessage(user : ArrayList<Users>)
    fun getLastUserInfo(userList : ArrayList<Users>)

}