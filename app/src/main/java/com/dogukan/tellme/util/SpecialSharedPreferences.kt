package com.dogukan.tellme.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class SpecialSharedPreferences {
    companion object{
        private var sharedPreferences : SharedPreferences ?= null
        @Volatile private var instance : SpecialSharedPreferences?=null
        private val lock = Any()
        operator fun invoke(context: Context) : SpecialSharedPreferences = instance ?: synchronized(lock){
            instance?: createdSSP(context)
        }
        private fun  createdSSP(context: Context) : SpecialSharedPreferences{
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return SpecialSharedPreferences()
        }
    }
    @SuppressLint("CommitPrefEdits")
    fun saveTime(time : Long){
        sharedPreferences?.edit(){
            putLong("time",time)
        }
    }
    fun getTime() = sharedPreferences?.getLong("time",0)
}