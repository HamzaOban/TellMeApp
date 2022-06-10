package com.dogukan.tellme.view

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation

import androidx.recyclerview.widget.LinearLayoutManager

import com.dogukan.tellme.NewMessagesRVAdapter

import com.dogukan.tellme.databinding.FragmentNewMessagesBinding

import com.dogukan.tellme.models.Users
import com.dogukan.tellme.repository.UserRepository
import com.dogukan.tellme.repository.UserRepositoryI
import com.dogukan.tellme.viewmodel.UserListViewModel
import kotlinx.android.synthetic.main.fragment_latest_messages.*
import kotlinx.android.synthetic.main.fragment_new_messages.*

class NewMessagesFragment : Fragment() {
    private val viewModel : UserListViewModel by viewModels()
    private lateinit var binding : FragmentNewMessagesBinding
    private var  mainActivityView : MainActivity = MainActivity()
    private lateinit var adapter : NewMessagesRVAdapter
    var newMessagesList = ArrayList<Users>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewMessagesBinding.inflate(layoutInflater)
        val activity = activity as? MainActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val action = NewMessagesFragmentDirections.actionNewMessagesFragmentToLatestMessagesFragment2()
                this.view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
            }
            android.R.id.undo -> {
                val action =
                    NewMessagesFragmentDirections.actionNewMessagesFragmentToLatestMessagesFragment2()
                this.view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
            }

        }
        return super.onOptionsItemSelected(item)
    }
    private fun searchViewOnQuery(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
              return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
               return false
            }

        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView4.layoutManager = LinearLayoutManager(context)

        viewModel.getUser()
        viewModel.getAllUsers()
        adapter = NewMessagesRVAdapter(newMessagesList)
        searchViewOnQuery()
        binding.recyclerView4.adapter = adapter
        observeLiveData()
        binding.swipeRefleshLayoutNewMessages.setOnRefreshListener {
            binding.newLoadingBar.visibility = View.VISIBLE
            binding.recyclerView4.visibility = View.GONE
            binding.newInformationTV.visibility = View.GONE
            viewModel.getAllUsers()
            viewModel.getUser()
            swipeRefleshLayoutNewMessages.isRefreshing=false
        }
    }
    private fun observeLiveData(){
        viewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.newInformationTV.visibility = View.GONE
                binding.recyclerView4.visibility = View.VISIBLE

                adapter.UsersListUpdate(it)
            }

        })
        viewModel.informationMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it){
                    binding.newInformationTV.text = "Kullanıcı Listen Boş"
                }
                else{
                    binding.newInformationTV.text = ""
                }
            }
        })
        viewModel.userLoading.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it){
                    binding.newLoadingBar.visibility = View.VISIBLE
                }
                else
                {
                    binding.newLoadingBar.visibility = View.GONE
                }
            }
        })
    }
    override fun onStop() {
        super.onStop()
        mainActivityView.activeState("offline")
    }
    override fun onDestroy() {
        super.onDestroy()
        mainActivityView.activeState("offline")
    }
    override fun onResume() {
        super.onResume()
        mainActivityView.activeState("online")
    }


}