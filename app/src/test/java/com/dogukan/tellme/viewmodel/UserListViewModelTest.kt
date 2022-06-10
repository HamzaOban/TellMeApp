package com.dogukan.tellme.viewmodel

import android.app.Application
import com.dogukan.tellme.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.coroutineContext

@ExperimentalCoroutinesApi
class UserListViewModelTest {
    private  lateinit var viewModel: UserListViewModel

    @Before
    fun setup(){
        viewModel = UserListViewModel(Application())

    }
    @Test
    fun userloading(){
        //viewModel.getAllUsers()
        val value = viewModel.getAllUsers().getOrAwaitValue()
        assertThat(viewModel.userLoading).isEqualTo(false)
    }

}