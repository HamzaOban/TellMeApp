package com.dogukan.tellme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.repository.UserInfoRepository

class RegisterViewModel : ViewModel() {
    private var userInfoRepository = UserInfoRepository.StaticFunction.getInstance()

   /* fun getUser() : LiveData<Users>{
        return userInfoRepository.getUser()
    }*/
}