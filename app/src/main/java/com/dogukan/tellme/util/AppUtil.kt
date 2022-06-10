package com.dogukan.tellme.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class AppUtil {
     fun getUID() : String?{
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid

    }
}