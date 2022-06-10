package com.dogukan.tellme.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.repository.LatestRepository
import com.dogukan.tellme.repository.LatestRepositoryI
import com.dogukan.tellme.service.LatestMessageDatabase
import com.dogukan.tellme.service.UsersDatabase
import com.dogukan.tellme.util.SpecialSharedPreferences
import kotlinx.coroutines.launch

class LatestMessagesViewModel(application: Application) : BaseViewModel(application) ,LatestRepositoryI{
    private  var latestRepository = LatestRepository(this)
    val latestMessage = MutableLiveData<List<ChatMessage>>()
    val latestUser = MutableLiveData<List<Users>>()
    val userLoading = MutableLiveData<Boolean>()
    val informationMessage = MutableLiveData<Boolean>()
    private val specialSharedPreferences = SpecialSharedPreferences(getApplication())
    private var updateTimeValue = 5 * 60 * 1000 * 1000 * 1000L

    fun listenForLatestMessages(){
        latestRepository.listenForLatestMessages()
    }

    fun refreshRecyclerViewUserInLatestMessage(){
        latestRepository.refreshRecyclerViewMessagesInUser()
    }
    fun refreshRecyclerViewMessage(){
        latestRepository.refreshRecyclerViewMessages()
    }
    fun currentUser() {
        latestRepository.fetchCurrentUser()
    }
    override fun showLatestMessage(message: ArrayList<ChatMessage>) {

        latestMessage.value = message
        informationMessage.value = message.size==0
        userLoading.value = false
    }
    fun getUserInfo() : LiveData<List<Users>>{
        return latestUser
    }
    override fun showUserInfoLatestMessage(user: ArrayList<Users>) {
        latestUser.value=user
    }

    override fun getLastUserInfo(userList: ArrayList<Users>) {
        TODO("Not yet implemented")
    }
    private fun saveSQLite(latestMessageList : List<ChatMessage>, latestUsersInfoList : List<Users>) {
       /* launch {
            val dao = LatestMessageDatabase(getApplication()).latestMessageDao()
            dao.deleteAllLatestMessage()
            dao.insertAllLatestMessage(*latestMessageList.toTypedArray(), latestUserInfo = latestUsersInfoList)
        }
        specialSharedPreferences.saveTime(System.nanoTime())*/
    }

}