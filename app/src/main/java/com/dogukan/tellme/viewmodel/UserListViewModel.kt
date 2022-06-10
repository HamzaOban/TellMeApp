package com.dogukan.tellme.viewmodel
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.repository.UserRepository
import com.dogukan.tellme.repository.UserRepositoryI
import com.dogukan.tellme.service.UsersDatabase
import com.dogukan.tellme.util.SpecialSharedPreferences
import kotlinx.coroutines.launch

class UserListViewModel(application: Application) : BaseViewModel(application), UserRepositoryI {
    private var userRepository = UserRepository(this)
    val users = MutableLiveData<List<Users>>()
    val userLoading = MutableLiveData<Boolean>()
    val informationMessage = MutableLiveData<Boolean>()

    private val specialSharedPreferences = SpecialSharedPreferences(getApplication())
    private var updateTimeValue = 0.1 * 60 * 1000 * 1000 * 1000L
    fun getAllUsers() : LiveData<List<Users>>{
        return users
    }
    fun getUser(){
        val getTime = specialSharedPreferences.getTime()
        if (getTime !=null && getTime!=0L && System.nanoTime()-getTime<updateTimeValue){
            //get SqLite
            getDataSQlite()
        }
        else{
            //get Firebase
            userRepository.getUserFirebase()
            getAllUsers().value?.let { saveSQLite(it) }

        }

    }
    private fun getDataSQlite(){
        launch {
            val userList = UsersDatabase(getApplication()).usersDao().getAllUser()
            if (userList.isEmpty()){
                userRepository.getUserFirebase()
                getAllUsers().value?.let { saveSQLite(it) }
            }
            users.value = userList
            informationMessage.value = userList.isEmpty()
            userLoading.value = false
        }
    }
    override fun showListOfUser(userList: ArrayList<Users>) {
        users.value = userList
        informationMessage.value = userList.size==0
        userLoading.value = false
    }
    private fun saveSQLite(userList : List<Users>) {
        launch {
            val dao = UsersDatabase(getApplication()).usersDao()
            dao.deleteAllUser()
            dao.insertAllUser(*userList.toTypedArray())
        }
        specialSharedPreferences.saveTime(System.nanoTime())
    }
}