package com.dogukan.tellme

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.dogukan.tellme.databinding.FragmentNewMessagesBinding
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.util.Addition
import com.dogukan.tellme.view.NewMessagesFragmentDirections

import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class NewMessagesRVAdapter(private val userList: ArrayList<Users>) : RecyclerView.Adapter<NewMessagesRVAdapter.NewMessageViewHolder>(),Filterable{
    var userFilterList = ArrayList<Users>()
    lateinit var mContext: Context


    init {
        userFilterList = userList
    }
    private val addition = Addition()
    class NewMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView = itemView.findViewById(R.id.message_imageView)
        var itemTitle : TextView = itemView.findViewById(R.id.message_TV)
        var itemStatus: TextView = itemView.findViewById(R.id.status_TV)
        var itemProgressBar : ProgressBar = itemView.findViewById(R.id.new_messages_progressBar)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_row_new_message,parent,false)
        return NewMessageViewHolder(view)
    }
    override fun getItemCount(): Int {
        return userFilterList.size
    }
    override fun onBindViewHolder(holder: NewMessageViewHolder, position: Int){
        holder.itemTitle.text = userFilterList[position].username
        holder.itemStatus.text = userFilterList[position].status
        addition.picassoUseIt(userFilterList[position].profileImageURL,holder.itemImage,holder.itemProgressBar)
        holder.itemProgressBar.visibility = View.GONE
        holder.itemView.setOnClickListener {
            val action = NewMessagesFragmentDirections.actionNewMessagesFragmentToChatLogFragment(
                position,
                userFilterList[position].uid,
                userFilterList[position].username,
                userFilterList[position].profileImageURL,
                userFilterList[position].status,
                userFilterList[position].activeState,
                userFilterList[position].Email,
                userFilterList[position].token!!
            )
            Navigation.findNavController(it).navigate(action)
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    fun UsersListUpdate(NewUserList : List<Users>){
        userList.clear()
        userList.addAll(NewUserList)

        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    userFilterList = userList
                } else {
                    val resultList = ArrayList<Users>()
                    for (row in userList) {
                        if (row.username.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    userFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = userFilterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                userFilterList = results?.values as ArrayList<Users>
                notifyDataSetChanged()
            }

        }
    }
}
