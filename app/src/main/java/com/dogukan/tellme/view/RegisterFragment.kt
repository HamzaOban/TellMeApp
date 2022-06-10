package com.dogukan.tellme.view

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.dogukan.tellme.databinding.FragmentRegisterBinding
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.util.AppUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val appUtil = AppUtil()
    var selectedPhotoUri : Uri?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.alreadyAccountTV.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            Navigation.findNavController(it).navigate(action)
        }
        init()
    }

    private fun init(){
        binding.registerbtn.setOnClickListener {

            performRegister()
        }

        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            if(it==null){
                return@registerForActivityResult
            }
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, it)
            val bitmapDrawable = BitmapDrawable(bitmap)
            binding.registerCircleImageView.setImageDrawable(bitmapDrawable)

            selectedPhotoUri = it
            binding.selectPhotobtn.setImageDrawable(null)
        }
        binding.selectPhotobtn.setOnClickListener {
            getImage.launch("image/*")
        }


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
    private fun performRegister(){
        val email = binding.RegisterEmailET.text.toString().trim()
        val password = binding.RegisterPasswordET.text.toString().trim()
        val username = binding.RegisterUserNameET.text.toString()
        if(username.isEmpty()){
            binding.RegisterUserNameET.setError("Please enter your username")
            return
        }
        if (!isEmailValid(email)){
            binding.RegisterEmailET.setError("Please enter a valid email address")
            return
        }
        if(email.isEmpty()){
            binding.RegisterEmailET.setError("Please enter your email")
            return
        }
        if (email.isBlank()){
            binding.RegisterEmailET.setError("no spaces can be left")
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

                Toast.makeText(context, "Registration Successful", Toast.LENGTH_LONG).show()
                Log.d("RegisterActivity", "Created user with uid:${it.result.user?.uid}")
                uploudImageToFirebaseStorage()

            }
            .addOnFailureListener {
                Toast.makeText(context, "this email already exists", Toast.LENGTH_LONG).show()
            }
    }
    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }
    private fun goToLatestMessageFragment(){
        val action = RegisterFragmentDirections.actionRegisterFragmentToLatestMessagesFragment2()
        view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
    }
    private fun uploudImageToFirebaseStorage(){

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        if (selectedPhotoUri==null){
            val uri = Uri.parse("android.resource://com.dogukan.tellme/drawable/images");
            val inputStream = context?.contentResolver?.openInputStream(uri)
            Log.d("draven","Girdi")
            ref.putStream(inputStream!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("draven","Girdi3")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
        }else{
            ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {


                    saveUserToFirebaseDatabase(it.toString())
                    Log.d("RegisterActivityy", "Successfully uploaded image : $it")
                }
            }
            .addOnFailureListener {
                Log.d("RegisterActivityy", "Successfully uploaded image : ${it.message}")
            }
        }


    }
    private fun showProgressBar(){
        binding.RegisterEmailET.visibility=View.GONE
        binding.RegisterPasswordET.visibility=View.GONE
        binding.RegisterUserNameET.visibility=View.GONE
        binding.alreadyAccountTV.visibility = View.GONE
        binding.selectPhotobtn.visibility = View.GONE
        binding.registerbtn.visibility = View.GONE
        binding.registerProgressBar.visibility = View.VISIBLE
    }
    private fun saveUserToFirebaseDatabase(profileImageURL: String){

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            val token = it
            val uid = FirebaseAuth.getInstance().uid ?: ""
            val email = FirebaseAuth.getInstance().currentUser?.email
            val status = "Welcome Tellme"
            val ref = FirebaseDatabase.getInstance().getReference("/users/${appUtil.getUID()}")
            val user = Users(
                uid, binding.RegisterUserNameET.text.toString(), profileImageURL, status,
                email.toString(), "Online", token
            )
            ref.setValue(user)
                .addOnSuccessListener {
                    showProgressBar()
                    Handler().postDelayed(Runnable {
                        // Code to start new activity and finish this one
                        goToLatestMessageFragment()
                    }, 1500L)

                }
        }

    }

}