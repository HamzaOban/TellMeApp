package com.dogukan.tellme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dogukan.tellme.view.ChatLogActivity
import com.dogukan.tellme.models.Users
import com.squareup.picasso.Picasso

class NewMessagesRVAdapter(private val userList: ArrayList<Users>) : RecyclerView.Adapter<NewMessagesRVAdapter.NewMessageViewHolder>(){

    class NewMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView = itemView.findViewById(R.id.message_imageView)
        var itemTitle : TextView = itemView.findViewById(R.id.message_TV)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_row_new_message,parent,false)
        return NewMessageViewHolder(view)
    }
    override fun getItemCount(): Int {
        return userList.size
    }
    override fun onBindViewHolder(holder: NewMessageViewHolder, position: Int){
        holder.itemTitle.text = userList[position].username
        Picasso.get().load(userList[position].profileImageURL).into(holder.itemImage)
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ChatLogActivity::class.java)
            intent.putExtra("USER_NAME",NewMessageViewHolder(it).itemTitle.text)
            intent.putExtra("USER_ID",userList[position].uid)
            it.context.startActivity(intent)

        }

    }
}
