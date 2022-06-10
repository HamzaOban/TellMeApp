package com.dogukan.tellme.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.dogukan.tellme.R
import com.dogukan.tellme.adapter.LatestMessagesRVAdapter
import com.dogukan.tellme.databinding.FragmentLatestMessagesBinding
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.viewmodel.LatestMessagesViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_latest_messages.*


class LatestMessagesFragment : Fragment() {
    private val viewModel : LatestMessagesViewModel by viewModels()
    val mainActivity : MainActivity = MainActivity()
    private lateinit var binding : FragmentLatestMessagesBinding
    private lateinit var adapter : LatestMessagesRVAdapter
    var lastestChatMessageList = ArrayList<ChatMessage>()
    var userList = ArrayList<Users>()

    companion object{
        var users : Users ?= null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        viewModel.currentUser()
    }
    private fun init(){

        val linearLayoutManager :  LinearLayoutManager = LinearLayoutManager(context)

        binding.recyclerView3.layoutManager = linearLayoutManager
        viewModel.getUserInfo()
        viewModel.currentUser()
        viewModel.listenForLatestMessages()
        viewModel.refreshRecyclerViewMessage()
        viewModel.refreshRecyclerViewUserInLatestMessage()


        adapter = LatestMessagesRVAdapter(lastestChatMessageList,userList)

        val activity = activity as? MainActivity
        activity?.supportActionBar?.setTitle(R.string.app_name)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.recyclerView3.adapter = adapter
        observeLiveData()
        setSwipeRefleshLayout()
        binding.bottomNavigation.selectedItemId = R.id.chat;

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.chat ->{
                    return@setOnItemSelectedListener true
                }
                R.id.settings ->{
                    val action = LatestMessagesFragmentDirections.actionLatestMessagesFragment2ToSettingsFragment()
                    view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
                    return@setOnItemSelectedListener true
                }

            }
            return@setOnItemSelectedListener true
        }

    }
    private fun setSwipeRefleshLayout(){
        binding.swipeRefleshLayout.setOnRefreshListener {
            binding.loadingBar.visibility = View.VISIBLE
            binding.recyclerView3.visibility = View.GONE
            binding.informationTV.visibility = View.GONE
            viewModel.listenForLatestMessages()
            viewModel.refreshRecyclerViewMessage()
            swipeRefleshLayout.isRefreshing=false

        }
    }
    private fun observeLiveData(){
        viewModel.latestMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.loadingBar.visibility = View.GONE
                binding.informationTV.text = ""
                binding.recyclerView3.visibility = View.VISIBLE
                adapter.latestMessagesUpdate(it)

            }
        })
        viewModel.latestUser.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.loadingBar.visibility = View.GONE
                binding.informationTV.text = ""
                binding.recyclerView3.visibility = View.VISIBLE
                adapter.latestUserUpdate(it)

            }
        })
        viewModel.informationMessage.observe(viewLifecycleOwner, Observer {
            binding.informationTV.visibility = View.VISIBLE
            it?.let {
                if (it){
                    binding.informationTV.setText(R.string.message_box_empty)
                }
                else{
                    binding.informationTV.text = ""
                }
            }
        })
        viewModel.userLoading.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it){
                    binding.loadingBar.visibility = View.VISIBLE
                }
                else
                {
                    binding.loadingBar.visibility = View.GONE
                }
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.show()
        setHasOptionsMenu(true)
        binding = FragmentLatestMessagesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mainActivity.activeState("online")
    }

    override fun onResume() {
        super.onResume()
        mainActivity.activeState("online")
    }
    override fun onStop() {
        super.onStop()
        mainActivity.activeState("offline")
    }
    override fun onDestroy() {
        super.onDestroy()
        mainActivity.activeState("offline")
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.top_nav_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_new_message ->{
                val action = LatestMessagesFragmentDirections.actionLatestMessagesFragment2ToNewMessagesFragment()
                view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
            }
            R.id.menu_sign_out ->{
                mainActivity.activeState("offline")
                FirebaseAuth.getInstance().signOut()
                val action = LatestMessagesFragmentDirections.actionLatestMessagesFragment2ToRegisterFragment()

                view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
            }
        }

        return super.onOptionsItemSelected(item)
    }

}