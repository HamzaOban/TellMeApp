package com.dogukan.tellme.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users

@Dao
interface ChatMessageDAO {
    @Insert
    suspend fun insertAllMessage(vararg chatMessage : ChatMessage) : List<Long>

    @Query("SELECT * FROM ChatMessage")
    suspend fun getAllChatMessage() : List<ChatMessage>

    @Query("DELETE FROM ChatMessage")
    suspend fun deleteAllChatMessage()
}