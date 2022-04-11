package com.dogukan.tellme.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogukan.tellme.adapter.ChatLogRVAdapter
import com.dogukan.tellme.databinding.ActivityChatLogBinding
import com.dogukan.tellme.models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList

class ChatLogActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatLogBinding
    private var adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    var chatMessageList = ArrayList<ChatMessage>()
    private var layoutManager : RecyclerView.LayoutManager?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val username = intent.getStringExtra("USER_NAME")
        binding.recyclerView2.scrollToPosition(chatMessageList.count()-1)
        supportActionBar?.title =username
        layoutManager = LinearLayoutManager(this)
        adapter = ChatLogRVAdapter(chatMessageList)
        binding.sendmassageBtn.setOnClickListener {
            performSendMessage()
        }
        listenForMessage()
        binding.recyclerView2.adapter = adapter
    }

    private fun listenForMessage(){
        val toID = intent.getStringExtra("USER_ID")
        val fromID = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    chatMessageList.add(chatMessage)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }
    private fun performSendMessage(){
        val text = binding.sendmassegeTV.text
        val toID = intent.getStringExtra("USER_ID")
        val fromID = FirebaseAuth.getInstance().uid
        val Ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()
        if (fromID==null){
            return
        }
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val timeStamp = "$hour:$minute"
        val chatMessage =
            ChatMessage(Ref.key!!, text.toString(), fromID, toID.toString(), timeStamp)
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

}