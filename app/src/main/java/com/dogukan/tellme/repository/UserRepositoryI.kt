package com.dogukan.tellme.repository

import com.dogukan.tellme.models.Users

interface UserRepositoryI {
    fun showListOfUser(userList : ArrayList<Users>)
}