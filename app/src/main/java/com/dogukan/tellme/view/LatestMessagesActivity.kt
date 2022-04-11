package com.dogukan.tellme.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogukan.tellme.R
import com.dogukan.tellme.adapter.LatestMessagesRVAdapter
import com.dogukan.tellme.databinding.ActivityLatestMessagesBinding
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LatestMessagesActivity : AppCompatActivity() {

    lateinit var binding : ActivityLatestMessagesBinding
    private var adapter : RecyclerView.Adapter<LatestMessagesRVAdapter.LatestMessagesViewHolder>? = null
    private var layoutManager : RecyclerView.LayoutManager?=null
    var LastestChatMessageList = ArrayList<ChatMessage>()
    val latestMessagesMap = HashMap<String, ChatMessage>()
    companion object{
        var CurrentUser: Users? =null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatestMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val actionBar: ActionBar? = actionBar
        //actionBar?.setDisplayHomeAsUpEnabled(true)
        layoutManager = LinearLayoutManager(this)

        listenForLatesMessages()
        binding.recyclerView3.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        fetchCurrentUser()
        verifyUserIsLoggedIn()

    }
    private fun refreshRecyclerViewMessages(){
        LastestChatMessageList.clear()
        latestMessagesMap.values.forEach {

            LastestChatMessageList.add(it)
            Log.d("saaa", LastestChatMessageList[0].TimeStamp)

        }
    }
    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CurrentUser = snapshot.getValue(Users::class.java)
                Log.d("hamza", "Current User ${CurrentUser?.username}")
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun listenForLatesMessages(){
        val fromID = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                adapter = LatestMessagesRVAdapter(LastestChatMessageList)
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage!=null){

                    latestMessagesMap[snapshot.key!!]=chatMessage

                    refreshRecyclerViewMessages()

                }

                binding.recyclerView3.adapter = adapter

            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                adapter = LatestMessagesRVAdapter(LastestChatMessageList)
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage!=null){

                    latestMessagesMap[snapshot.key!!]=chatMessage
                    refreshRecyclerViewMessages()

                }
                binding.recyclerView3.adapter = adapter
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid==null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_nav_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when( item.itemId){
            R.id.menu_new_message ->{
                val intent = Intent(this, NewMessagesActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}