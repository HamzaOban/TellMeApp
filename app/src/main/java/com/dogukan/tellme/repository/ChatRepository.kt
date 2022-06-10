package com.dogukan.tellme.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dogukan.tellme.databinding.FragmentChatLogBinding
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.util.AppUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds

class ChatRepository(ChatRepositoryI: ChatRepositoryI) {
    private var chatRepositoryI : ChatRepositoryI ?= ChatRepositoryI
    private var chatMessageList = ArrayList<ChatMessage>()
    private val chatMessagesMap = HashMap<String, ChatMessage>()
    private  var AppUtil = AppUtil()

     fun listenForMessageAll(toID : String){
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/${AppUtil.getUID()}/$toID")
         chatMessageList.clear()
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    chatMessageList.add(chatMessage)
                    chatRepositoryI?.showListOfMessage(chatMessageList)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    fun listenForMessage(toID : String){
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/${AppUtil.getUID()}/$toID")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {

                    chatMessageList.add(chatMessage)
                    chatRepositoryI?.showListOfMessage(chatMessageList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    fun deleteMessage(toID : String, message : String, chatMessageList : ArrayList<ChatMessage>, position : Int){
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/${AppUtil.getUID()}/$toID")
        val toref = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/${AppUtil.getUID()}")
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/${AppUtil.getUID()}/$toID")
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toID/${AppUtil.getUID()}")

        toref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)


                    if (chatMessage?.text==chatMessageList[position].text){
                        toref.child(snapshot.key!!).removeValue()
                    }


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                    if (chatMessage?.text==chatMessageList[position].text){
                        toref.child(snapshot.key!!).removeValue()
                    }

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                    if (chatMessage?.text==chatMessageList[position].text){
                        toref.child(snapshot.key!!).removeValue()
                    }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        ref.child(chatMessageList[position].id).removeValue()
       // toref.child(chatMessageList[position].id).removeValue()
        chatRepositoryI?.deleteMessage(true)
    }

    fun seenMessage(toID : String){
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/").child(AppUtil.getUID()!!).child(toID)
        val toref = FirebaseDatabase.getInstance().getReference("/user-messages/").child(toID).child(AppUtil.getUID()!!)

        toref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage!=null){
                    if (chatMessage.ToID == AppUtil.getUID()){
                        snapshot.ref.child("seen").setValue(true)
                        chatRepositoryI?.checkIsSeen(true)
                    }
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

     fun getActiveState(toID: String){
        val databaseref = FirebaseDatabase.getInstance().getReference("users").child(toID)
        databaseref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val activeState = snapshot.child("activeState").value.toString()

                chatRepositoryI?.checkActiveState(activeState)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    fun getActiveTyping(toID: String){
        val databaseref = FirebaseDatabase.getInstance().getReference("users").child(toID)
        databaseref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val activeState = snapshot.child("typing").value.toString()
                chatRepositoryI?.checkIsTyping(activeState)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    fun setActiveTyping(isTyping : String){
        val ref = FirebaseDatabase.getInstance().getReference("users").child(AppUtil.getUID()!!)
        val hashMap : MutableMap<String,String> = HashMap()
        hashMap["typing"] = isTyping
        ref.updateChildren(hashMap as Map<String, Any>)

    }

     fun performSendMessage(text : String, toID: String,binding: FragmentChatLogBinding){

        val fromID = FirebaseAuth.getInstance().uid
        val Ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()
        if (fromID==null){
            return
        }
         val c = System.currentTimeMillis();

        val chatMessage = ChatMessage(Ref.key!!, text, fromID.toString(), toID, c,false,"TEXT","no")
        Ref.setValue(chatMessage)
            .addOnSuccessListener {
                binding.sendmassegeTV.text.clear()
                binding.recyclerView2.scrollToPosition(chatMessageList.count()-1)
            }
        toRef.setValue(chatMessage)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID/$toID")
        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toID/$fromID")
        latestMessageToRef.setValue(chatMessage)

    }
     fun uploudImageToFirebaseStorage(uri : Uri ,toID: String){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/chatImages/$filename")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    performSendImage(it.toString(),toID)
                }
            }
            .addOnFailureListener {
            }
    }
    fun performSendImage(imageUrl : String, toID: String){

        val fromID = FirebaseAuth.getInstance().uid
        val Ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()
        if (fromID==null){
            return
        }
        val c = System.currentTimeMillis();

        val chatMessage = ChatMessage(Ref.key!!, imageUrl, fromID.toString(), toID, c,false,"IMAGE","no")
        Ref.setValue(chatMessage)
            .addOnSuccessListener {
                chatRepositoryI?.sendImage(true)
            }
            .addOnFailureListener {
                chatRepositoryI?.sendImage(false)
            }
        toRef.setValue(chatMessage)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID/$toID")
        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toID/$fromID")
        latestMessageToRef.setValue(chatMessage)

    }



}