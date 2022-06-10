package com.dogukan.tellme.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users

@Dao
interface LatestMessageDAO {
   /* @Insert
    suspend fun insertAllLatestMessage(vararg latestMessageInfo : ChatMessage,  latestUserInfo : List<Users>) : List<Long>*/

   /* @Query("SELECT * FROM Users, ChatMessage")
    suspend fun getAllLatestMessage() : List<Users>*/

   /* @Query("DELETE FROM Users.ChatMessage")
    suspend fun deleteAllLatestMessage()*/
}