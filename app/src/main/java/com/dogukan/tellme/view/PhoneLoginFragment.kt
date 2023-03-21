package com.dogukan.tellme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.dogukan.tellme.R
import com.dogukan.tellme.databinding.FragmentPhoneLoginBinding
import com.dogukan.tellme.util.AppUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.HashMap

class PhoneLoginFragment : Fragment() {
    private lateinit var binding : FragmentPhoneLoginBinding
    private val appUtil = AppUtil()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhoneLoginBinding.inflate(inflater)
        return binding.root
    }
    private fun goToLatestMessageFragment(){
        val action = PhoneLoginFragmentDirections.actionPhoneLoginFragmentToLatestMessagesFragment2()
        view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser!=null){
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                val token = it
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("users")
                        .child(appUtil.getUID()!!)
                val map: MutableMap<String, Any> = HashMap()
                map["token"] = token
                databaseReference.updateChildren(map)
                goToLatestMessageFragment()
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.phoneButton.setOnClickListener {
            goToOtherPhoneFragment()
        }
    }
    private fun goToOtherPhoneFragment(){
        val action = PhoneLoginFragmentDirections.actionPhoneLoginFragmentToPhoneLogin2Fragment()
        view?.let { Navigation.findNavController(it).navigate(action) }
    }
}