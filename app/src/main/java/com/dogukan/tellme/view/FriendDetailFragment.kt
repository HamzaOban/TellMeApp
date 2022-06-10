package com.dogukan.tellme.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.dogukan.tellme.R
import com.dogukan.tellme.databinding.FragmentFriendDetailBinding
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.util.AppUtil
import com.dogukan.tellme.viewmodel.SettingsViewModel

class FriendDetailFragment : Fragment() {

    private lateinit var ToID : String
    private var position : Int = 0
    private lateinit var myName : String
    private lateinit var hisImage : String
    private lateinit var ActiveState : String
    private lateinit var status : String
    private lateinit var email : String
    private lateinit var token : String
    private lateinit var user : Users
    private val appUtil = AppUtil()
    private val viewModel : SettingsViewModel by viewModels()
    private lateinit var binding : FragmentFriendDetailBinding
    private var  mainActivityView : MainActivity = MainActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friend_detail, container, false)
        arguments?.let {
            position = FriendDetailFragmentArgs.fromBundle(it).position
            ToID = FriendDetailFragmentArgs.fromBundle(it).toID
            myName = FriendDetailFragmentArgs.fromBundle(it).userName
            hisImage = FriendDetailFragmentArgs.fromBundle(it).imageURL
            ActiveState = FriendDetailFragmentArgs.fromBundle(it).activeState
            status = FriendDetailFragmentArgs.fromBundle(it).status
            email = FriendDetailFragmentArgs.fromBundle(it).email
            token = FriendDetailFragmentArgs.fromBundle(it).token
        }
        user = Users(appUtil.getUID()!!, myName, hisImage, status, email, ActiveState, "")
        observeLiveData()
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action = FriendDetailFragmentDirections.actionFriendDetailFragmentToChatLogFragment(position,ToID,myName, hisImage,status,ActiveState,email,token)
                view?.let { Navigation.findNavController(it).navigate(action) }
            }
        })
        return binding.root
    }
    private fun observeLiveData(){
        viewModel.getUser(ToID).observe(viewLifecycleOwner, Observer {
            binding.userModel = it
            binding.settingsProgressBar.visibility = View.GONE

        })
    }
    override fun onResume() {
        super.onResume()
        mainActivityView.activeState("online")
        viewModel.getUser(ToID)

    }

    override fun onStop() {
        super.onStop()
        mainActivityView.activeState("offline")
        viewModel.getUser(ToID)

    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivityView.activeState("offline")
        viewModel.getUser(ToID)
    }
}