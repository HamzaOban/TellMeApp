package com.dogukan.tellme.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukan.tellme.databinding.FragmentChatLogBinding
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.repository.ChatRepository
import com.dogukan.tellme.repository.ChatRepositoryI
import com.dogukan.tellme.service.ChatMessageDatabase
import com.dogukan.tellme.service.UsersDatabase
import com.dogukan.tellme.util.SpecialSharedPreferences
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : BaseViewModel(application), ChatRepositoryI {
    private var chatRepository = ChatRepository(this)
    val message = MutableLiveData<List<ChatMessage>>()
    val activeState = MutableLiveData<Boolean>()
    val isSeen = MutableLiveData<Boolean>()
    val isTyping = MutableLiveData<Boolean>()
    val imageUpload = MutableLiveData<Boolean>()
    val deleteMessage = MutableLiveData<Boolean>()
    private val specialSharedPreferences = SpecialSharedPreferences(getApplication())
    private var updateTimeValue = 0.05 * 60 * 1000 * 1000 * 1000L

    fun setIsTyping(isTyping: String){
        chatRepository.setActiveTyping(isTyping)
    }
    fun getIsTyping(toID: String){
        chatRepository.getActiveTyping(toID)
    }
    fun getIstypingData() : LiveData<Boolean>{
        return isTyping
    }
    fun getActiveState() : LiveData<Boolean>{
        return activeState
    }
    fun getActiveStateFirebase (ToID: String){
        chatRepository.getActiveState(ToID)
    }
    fun getAllMessage() : LiveData<List<ChatMessage>> {
        return message
    }

    fun getMessageFirebase(ToID : String){
        chatRepository.listenForMessage(ToID)

    }
    fun getMessageFirebaseAll(ToID : String){
        val getTime = specialSharedPreferences.getTime()

        if (getTime !=null && getTime!=0L && System.nanoTime()-getTime<updateTimeValue){
            //get sql
            chatRepository.listenForMessageAll(ToID)
            getAllMessage().value?.let { saveSQLite(it) }
            if (getAllMessage().value?.isEmpty()==true){
                getDataSQlite(ToID)
            }
        }
        else{
            //get Firebase

            chatRepository.listenForMessageAll(ToID)
            getAllMessage().value?.let { saveSQLite(it) }

        }

    }
    fun getIsSeenStatus() : LiveData<Boolean>{
        return isSeen
    }
    fun deleteMessage(toID : String,message : String,list : ArrayList<ChatMessage>,positon : Int){
        chatRepository.deleteMessage(toID,message,list,positon)
    }
    fun performSendMessage(text : String, toID: String, binding: FragmentChatLogBinding){
        chatRepository.performSendMessage(text,toID,binding)
    }
    fun performSendImage(uri: Uri, toID: String){
        chatRepository.uploudImageToFirebaseStorage(uri , toID)
    }

    fun checkIsSeenMessage(toID: String){
        chatRepository.seenMessage(toID)
    }
    override fun showListOfMessage(messageList: ArrayList<ChatMessage>) {
        message.value = messageList
    }

    override fun checkActiveState(activeState: String){
        this.activeState.value = activeState.equals("online")

    }

    override fun checkIsSeen(isSeen: Boolean) {
        this.isSeen.value = isSeen
    }

    override fun checkIsTyping(isTyping: String) {
        this.isTyping.value = isTyping.equals("yes")

    }


    override fun sendMessage() {

    }

    override fun deleteMessage(isDeleted: Boolean) {
        deleteMessage.value = isDeleted
    }


    override fun sendImage(isUpload: Boolean) {
        imageUpload.value = isUpload
    }
    private fun saveSQLite(chatMessage : List<ChatMessage>) {
        launch {
            val dao = ChatMessageDatabase(getApplication()).ChatMessageDAO()
            dao.deleteAllChatMessage()
            dao.insertAllMessage(*chatMessage.toTypedArray())
        }
        specialSharedPreferences.saveTime(System.nanoTime())
    }
    private fun getDataSQlite(ToID: String){
        launch {
            if (getAllMessage().value?.isEmpty() == true){
                chatRepository.listenForMessageAll(ToID)
                getAllMessage().value?.let { saveSQLite(it) }
            }
            val chatMessage = ChatMessageDatabase(getApplication()).ChatMessageDAO().getAllChatMessage()
            if (chatMessage.isEmpty()){
                getMessageFirebase(ToID)
                chatRepository.listenForMessageAll(ToID)
                getAllMessage().value?.let { saveSQLite(it) }
            }
            message.value = chatMessage
        }
    }
}