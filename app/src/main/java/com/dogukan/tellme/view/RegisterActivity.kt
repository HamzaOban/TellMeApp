package com.dogukan.tellme.view

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dogukan.tellme.databinding.ActivityRegisterBinding
import com.dogukan.tellme.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegisterBinding
    var selectedPhotoUri : Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.registerbtn.setOnClickListener {

            performRegister()
        }
        binding.alreadyAccountTV.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                val bitmapDrawable = BitmapDrawable(bitmap)
                binding.selectPhotobtn.background = bitmapDrawable
                selectedPhotoUri = it
                binding.selectPhotobtn.setImageDrawable(null)
                //binding.selectphotoImageviewRegister.background = bitmapDrawable
                //binding.selectphotoImageviewRegister.alpha = 0f
            }
        )
        binding.selectPhotobtn.setOnClickListener {
            getImage.launch("image/*")

        }
    }

    private fun performRegister(){
        val email = binding.RegisterEmailET.text.toString()
        val password = binding.RegisterPasswordET.text.toString()
        val username = binding.RegisterUserNameET.text.toString()
        if(username.isEmpty()){
            binding.RegisterUserNameET.setError("Please enter your username")
            return
        }
        if(email.isEmpty()){
            binding.RegisterEmailET.setError("Please enter your email")
            return
        }
        if(password.isEmpty()){
            binding.RegisterPasswordET.setError("Please enter your password")
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful){
                    return@addOnCompleteListener
                }
                Log.d("RegisterActivity", "Created user with uid:${it.result.user?.uid}")
                uploudImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Toast.makeText(this, "this email already exists", Toast.LENGTH_LONG).show()

            }
    }
    private fun uploudImageToFirebaseStorage(){
        if(selectedPhotoUri==null){
            Log.d("RegisterActivity", "null")
            return
        }
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded image : ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "Successfully uploaded image : $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Successfully uploaded image : ${it.message}")
            }
    }
    private fun saveUserToFirebaseDatabase(profileImageURL: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = Users(uid, binding.RegisterUserNameET.text.toString(), profileImageURL)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "we saved user database")
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
    }
}