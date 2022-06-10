package com.dogukan.tellme.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.dogukan.tellme.R
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.repository.LatestRepository
import com.dogukan.tellme.repository.LatestRepositoryI
import com.dogukan.tellme.util.Addition
import com.dogukan.tellme.view.LatestMessagesFragmentDirections
import com.dogukan.tellme.view.NewMessagesFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LatestMessagesRVAdapter (private val chatMessage:ArrayList<ChatMessage> ,private val userList : ArrayList<Users>) :
    RecyclerView.Adapter<LatestMessagesRVAdapter.LatestMessagesViewHolder>(){


    private var addition = Addition()

    class LatestMessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var itemImage : CircleImageView = itemView.findViewById(R.id.latest_imageView)
        var itemUserName : TextView = itemView.findViewById(R.id.latest_username_TV)
        var itemLastestMessage : TextView = itemView.findViewById(R.id.latest_message_TV)
        var itemLastestMessageTimeStamp : TextView = itemView.findViewById(R.id.latest_messages_time_stampTV)
        var itemProgressBar : ProgressBar = itemView.findViewById(R.id.latest_progressBar)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestMessagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.latest_message_row,parent,false)
        return LatestMessagesViewHolder(view)
    }
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: LatestMessagesViewHolder, position: Int) {

        if (userList.size != 0 && userList.size == chatMessage.size){
            holder.itemUserName.text = userList[position].username
            addition.picassoUseIt(userList[position].profileImageURL,holder.itemImage,holder.itemProgressBar)
        }
        if (chatMessage[position].isSeen){
            holder.itemView.findViewById<ConstraintLayout>(R.id.latest_message_constraint_layout).setBackgroundResource(R.drawable.layout_bg_unread_message)
        }
        else{
            holder.itemView.findViewById<ConstraintLayout>(R.id.latest_message_constraint_layout).setBackgroundResource(R.drawable.layout_bg)
        }
        holder.itemProgressBar.visibility = View.VISIBLE
        val sdf = SimpleDateFormat("HH:mm")
        val netDate = Date(chatMessage[position].TimeStamp)
        val timestamp = sdf.format(netDate)

        holder.itemLastestMessageTimeStamp.text = timestamp
        if (chatMessage[position].type=="TEXT"){
            holder.itemLastestMessage.text = chatMessage[position].text
        }else{
            holder.itemLastestMessage.setText(R.string.Image)
        }

        holder.itemView.setOnClickListener {
            val action = LatestMessagesFragmentDirections.actionLatestMessagesFragment2ToChatLogFragment(position,
                userList[position].uid,
                userList[position].username,
                userList[position].profileImageURL,
                userList[position].status,
                userList[position].activeState,
                userList[position].Email,
                userList[position].token!!
            )
            Navigation.findNavController(it).navigate(action)
        }
    }


    override fun getItemCount(): Int {
        return chatMessage.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun latestMessagesUpdate(latesMessageList : List<ChatMessage>){
        chatMessage.clear()
        chatMessage.addAll(latesMessageList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun latestUserUpdate(userList: List<Users>){
        this.userList.clear()

        this.userList.addAll(userList)
        notifyDataSetChanged()
    }




}