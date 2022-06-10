package com.dogukan.tellme.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dogukan.tellme.models.Users

@Dao
interface UsersDAO {
    //Data Access Object
    @Insert
    suspend fun insertAllUser(vararg user : Users) : List<Long>

    @Query("SELECT * FROM Users")
    suspend fun getAllUser() : List<Users>

    @Query("DELETE FROM Users")
    suspend fun deleteAllUser()
}