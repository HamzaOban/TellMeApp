package com.dogukan.tellme.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.dogukan.tellme.R

import com.dogukan.tellme.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backToRegistrationTV.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.loginbtn.setOnClickListener {
            Login()
        }

    }
    private fun Login(){
        val email = binding.LoginEmailET.text.toString()
        val password = binding.LoginPasswordET.text.toString()

        if(email.isEmpty()){
            binding.LoginEmailET.setError("Please enter your email")
            return
        }
        if(password.isEmpty()){
            binding.LoginPasswordET.setError("Please enter your password")
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful){
                    return@addOnCompleteListener
                }
                val action = LoginFragmentDirections.actionLoginFragmentToLatestMessagesFragment2()
                view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
            }
            .addOnFailureListener {
                Log.d("Main", "login user with uid:${it.message}")
            }
    }

}