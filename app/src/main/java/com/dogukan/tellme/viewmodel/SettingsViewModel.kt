package com.dogukan.tellme.viewmodel

import android.net.Uri
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.repository.UserInfoRepository

class SettingsViewModel : ViewModel() {
    private var userInfoRepository = UserInfoRepository.StaticFunction.getInstance()

    fun getUser(userID : String) : LiveData<Users>{
        return userInfoRepository.getUser(userID)
    }
    fun changePhoto(selectedPhotoUri : Uri){
        userInfoRepository.changePhoto(selectedPhotoUri)
    }
    fun changeUserName(userName : String){
        userInfoRepository.updateUserName(userName)
    }
    fun changeStatus(Status : String){
        userInfoRepository.updateStatus(Status)
    }

}