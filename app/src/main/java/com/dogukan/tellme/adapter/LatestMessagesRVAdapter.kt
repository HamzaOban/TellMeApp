package com.dogukan.tellme.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dogukan.tellme.R
import com.dogukan.tellme.view.ChatLogActivity
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class LatestMessagesRVAdapter (private val chatMessage:ArrayList<ChatMessage>) : RecyclerView.Adapter<LatestMessagesRVAdapter.LatestMessagesViewHolder>() {

    class LatestMessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var itemImage : CircleImageView = itemView.findViewById(R.id.latest_imageView)
        var itemUserName : TextView = itemView.findViewById(R.id.latest_username_TV)
        var itemLastestMessage : TextView = itemView.findViewById(R.id.latest_message_TV)
        var itemLastestMessageTimeStamp : TextView = itemView.findViewById(R.id.latest_messages_time_stampTV)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestMessagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.latest_message_row,parent,false)
        return LatestMessagesViewHolder(view)
    }
    override fun onBindViewHolder(holder: LatestMessagesViewHolder, position: Int) {

        holder.itemLastestMessageTimeStamp.text = chatMessage[position].TimeStamp
        holder.itemLastestMessage.text = chatMessage[position].text
        val chatPartnerID : String

        if (chatMessage[position].fromID == FirebaseAuth.getInstance().uid){
            chatPartnerID = chatMessage[position].ToID
        }
        else
        {
            chatPartnerID = chatMessage[position].fromID
        }


        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerID")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                holder.itemUserName.text = user?.username

                Picasso.get().load(user?.profileImageURL).into(holder.itemImage)

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        holder.itemView.setOnClickListener {
            Log.d("LatestMessages","Selamlar")
            val intent = Intent(it.context, ChatLogActivity::class.java)
            intent.putExtra("USER_NAME", LatestMessagesViewHolder(it).itemUserName.text)
            intent.putExtra("USER_ID",chatPartnerID)
            it.context.startActivity(intent)
        }

    }
    override fun getItemCount(): Int {
        return chatMessage.size
    }
}