package com.dogukan.tellme.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.dogukan.tellme.R
import com.dogukan.tellme.util.AppUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private var firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    private val appUtil = AppUtil()

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)




        }
    fun activeState(activeState : String){
        if (firebaseAuth.currentUser!=null){
            val ref = FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUID()!!)
            val hashMap : MutableMap<String,String> = HashMap()
            hashMap["activeState"] = activeState
            ref.updateChildren(hashMap as Map<String, Any>)
        }



    }

    override fun onStart() {
        super.onStart()
        activeState("online")
    }
    override fun onResume() {
        super.onResume()
        activeState("online")

    }

    override fun onPause() {
        super.onPause()
        activeState("offline")

    }

    override fun onStop() {
        super.onStop()
        activeState("offline")
    }

    override fun onDestroy() {
        super.onDestroy()
        activeState("offline")
    }




    }
