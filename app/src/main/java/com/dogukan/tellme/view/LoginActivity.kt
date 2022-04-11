package com.dogukan.tellme.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dogukan.tellme.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {


    lateinit var binding : ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loginbtn.setOnClickListener {
            Login()
        }
        binding.backToRegistrationTV.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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
                Log.d("Main", "login with uid:${it.result.user?.uid}")
                var intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("Main", "login user with uid:${it.message}")
            }
    }
}