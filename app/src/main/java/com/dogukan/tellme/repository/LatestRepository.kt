package com.dogukan.tellme.repository

import android.util.Log
import com.dogukan.tellme.adapter.LatestMessagesRVAdapter
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.view.LatestMessagesFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LatestRepository(latestRepositoryI: LatestRepositoryI) {
    private var latestRepositoryI : LatestRepositoryI ?= latestRepositoryI
    private var latestMessageList = ArrayList<ChatMessage>()
    val latestMessagesMap = HashMap<String, ChatMessage>()
    private var latestUserInfoList = ArrayList<Users>()
    val latestUserInfoListMap = HashMap<String, Users>()


    fun listenForLatestMessages(){
        val fromID = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID")
        ref.orderByChild("timeStamp").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                latestMessageList.clear()

                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage!=null){
                    latestMessagesMap[snapshot.key!!]=chatMessage
                    latestRepositoryI?.showLatestMessage(latestMessageList)
                    refreshRecyclerViewMessages()
                }
                val chatPartner : String? = if (chatMessage?.fromID == fromID){
                    chatMessage?.ToID
                }else {
                    chatMessage?.fromID
                }

                val referance = FirebaseDatabase.getInstance().getReference("/users/$chatPartner")
                referance.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(Users::class.java)
                        if (user!=null) {
                            latestUserInfoListMap[snapshot.key!!] = user
                            latestRepositoryI?.showUserInfoLatestMessage(latestUserInfoList)
                            refreshRecyclerViewMessagesInUser()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                latestMessageList.clear()
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage!=null){
                    latestMessagesMap[snapshot.key!!]=chatMessage
                    latestRepositoryI?.showLatestMessage(latestMessageList)
                    refreshRecyclerViewMessages()
                }
                val chatPartner : String? = if (chatMessage?.fromID == fromID){
                    chatMessage?.ToID
                }else {
                    chatMessage?.fromID
                }
                val referance = FirebaseDatabase.getInstance().getReference("/users/$chatPartner")
                referance.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(Users::class.java)
                        if (user!=null) {
                            latestUserInfoListMap[snapshot.key!!] = user
                            latestRepositoryI?.showUserInfoLatestMessage(latestUserInfoList)
                            refreshRecyclerViewMessagesInUser()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    fun refreshRecyclerViewMessages(){
        latestMessageList.clear()
        latestMessagesMap.values.reversed().forEach {
            latestMessageList.add(it)

        }
        latestRepositoryI?.showLatestMessage(latestMessageList)
    }
    fun refreshRecyclerViewMessagesInUser(){
        latestUserInfoList.clear()
        latestUserInfoListMap.values.reversed().forEach {
            latestUserInfoList.add(it)
        }
        latestRepositoryI?.showUserInfoLatestMessage(latestUserInfoList)

    }
    fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                LatestMessagesFragment.users = snapshot.getValue(Users::class.java)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



}