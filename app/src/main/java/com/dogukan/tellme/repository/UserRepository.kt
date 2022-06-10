package com.dogukan.tellme.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.dogukan.tellme.NewMessagesRVAdapter
import com.dogukan.tellme.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.coroutineContext

class UserRepository(UserRepositoryI: UserRepositoryI) {

    private var userRepositoryI : UserRepositoryI ?= UserRepositoryI
    private  var userList = ArrayList<Users>()

    fun getUserFirebase(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                snapshot.children.forEach{
                    val user = it.getValue(Users::class.java)

                    if (user!=null && user.uid != FirebaseAuth.getInstance().uid){
                        userList.add(user)
                        userRepositoryI?.showListOfUser(userList)

                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("ViewModel",error.message)
            }
        })
    }




}