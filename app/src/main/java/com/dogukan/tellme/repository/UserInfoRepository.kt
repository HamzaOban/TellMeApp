package com.dogukan.tellme.repository

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.util.AppUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UserInfoRepository()  {
    private lateinit var databaseRef : DatabaseReference
    private var liveData : MutableLiveData<Users>?=null
    private val appUtil = AppUtil()

     fun getUser(userID : String?) : LiveData<Users>{
        if (liveData == null){
            liveData = MutableLiveData()
        }
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userID!!)
        databaseRef.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NullSafeMutableLiveData")
            override fun onDataChange(snapshot: DataSnapshot) {
                val userModel = snapshot.getValue(Users::class.java)
                liveData?.value = userModel
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        return liveData!!
    }
    fun changePhoto(selectedPhotoUri : Uri){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    updateProfileImage(it.toString())
                    Log.d("RegisterActivityy", "Successfully uploaded image : $it")
                }
            }
            .addOnFailureListener {
                    Log.d("RegisterActivityy", "Successfully uploaded image : ${it.message}")
            }
    }

    private fun updateProfileImage(profileImageURL: String){
        val ref = FirebaseDatabase.getInstance().getReference("/users/${appUtil.getUID()}")
        val hashMap : MutableMap<String,String> = HashMap()
        hashMap["profileImageURL"] = profileImageURL
        ref.updateChildren(hashMap as Map<String, Any>)
    }
    fun updateUserName(UserName: String){
        val ref = FirebaseDatabase.getInstance().getReference("/users/${appUtil.getUID()}")
        val hashMap : MutableMap<String,String> = HashMap()
        hashMap["username"] = UserName
        ref.updateChildren(hashMap as Map<String, Any>)
    }
    fun updateStatus(Status: String){
        val ref = FirebaseDatabase.getInstance().getReference("/users/${appUtil.getUID()}")
        val hashMap : MutableMap<String,String> = HashMap()
        hashMap["status"] = Status
        ref.updateChildren(hashMap as Map<String, Any>)
    }



    object StaticFunction{
        private var instance : UserInfoRepository?= null
        fun getInstance() : UserInfoRepository{
            if (instance == null){
                instance = UserInfoRepository()
            }
            return instance!!
        }
    }
}