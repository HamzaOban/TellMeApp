package com.dogukan.tellme.view


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogukan.tellme.NewMessagesRVAdapter
import com.dogukan.tellme.databinding.ActivityNewMessagesBinding
import com.dogukan.tellme.models.Users

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewMessagesActivity : AppCompatActivity() {

    lateinit var binding : ActivityNewMessagesBinding
    private var adapter : RecyclerView.Adapter<NewMessagesRVAdapter.NewMessageViewHolder>? = null
    private var layoutManager : RecyclerView.LayoutManager?=null
    var userList = ArrayList<Users>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        fetchUsers()
    }
    private fun init(){
        binding = ActivityNewMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Select User"
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        layoutManager = LinearLayoutManager(this)

    }
    //fetch data from users database
    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter = NewMessagesRVAdapter(userList)
                snapshot.children.forEach{
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(Users::class.java)
                    if (user!=null)
                    userList.add(user)
                }
                binding.recyclerView.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}