package com.dogukan.tellme.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.*
@Entity
class ChatMessage(
    @PrimaryKey(autoGenerate = false)
    val id : String,
    @ColumnInfo(name = "text")
    val text :String,
    @ColumnInfo(name = "fromID")
    val fromID : String,
    @ColumnInfo(name = "ToID")
    var ToID : String,
    @ColumnInfo(name = "TimeStamp")
    var TimeStamp : Long,
    @ColumnInfo(name = "isSeen")
    var isSeen : Boolean,
    @ColumnInfo(name = "type")
    var type : String,
    @ColumnInfo(name = "isTyping")
    var isTyping : String

    ) {
    constructor() : this("","","","", 1,false,"TEXT" ,"no")
}