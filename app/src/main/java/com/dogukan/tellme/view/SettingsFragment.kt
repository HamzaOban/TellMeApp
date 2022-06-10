package com.dogukan.tellme.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.dogukan.tellme.R
import com.dogukan.tellme.databinding.FragmentSettingsBinding
import com.dogukan.tellme.util.AppUtil
import com.dogukan.tellme.viewmodel.SettingsViewModel

class SettingsFragment : Fragment() {
    private lateinit var binding : FragmentSettingsBinding
    private val viewModel : SettingsViewModel by viewModels()
    private var  mainActivityView : MainActivity = MainActivity()
    private val appUtil = AppUtil()

    var selectedPhotoUri : Uri?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.selectedItemId = R.id.settings;
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.chat ->{
                    val action = SettingsFragmentDirections.actionSettingsFragmentToLatestMessagesFragment2()
                    view.let { it1 -> Navigation.findNavController(it1).navigate(action) }

                    return@setOnItemSelectedListener true
                }
                R.id.settings ->{

                    return@setOnItemSelectedListener true
                }

            }

            return@setOnItemSelectedListener true
        }
        changePhoto()
        changeUserName()
        changeStatus()
        deleteAccount()
    }
    private fun changePhoto(){
        val getImage = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                if(it==null){
                    return@ActivityResultCallback
                }
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver,it)
                val bitmapDrawable = BitmapDrawable(bitmap)
                binding.imgProfile.setImageDrawable(bitmapDrawable)
                selectedPhotoUri = it
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setMessage(R.string.change_picture)
                alertDialog.setTitle(R.string.change_picture_title)
                alertDialog.setPositiveButton("Yes",DialogInterface.OnClickListener{ dialog, _ ->
                    viewModel.changePhoto(selectedPhotoUri!!)
                    dialog.cancel()
                })
                alertDialog.setNegativeButton("No" ,DialogInterface.OnClickListener{ dialog, _ ->
                    dialog.cancel()
                })
                val alert = alertDialog.create()
                alert.show()
            }
        )
        binding.imgPickImage.setOnClickListener{
            getImage.launch("image/*")
        }
    }
    private fun changeUserName(){
        binding.userNameCard.setOnClickListener {
            Log.d("Card","UserName")
            mainActivityView = (activity as MainActivity)
            val bottomSheet = EditSettingsFragment(1)
            bottomSheet.show(mainActivityView.supportFragmentManager,"TAG")
        }
    }
    private fun changeStatus(){
        binding.statusCard.setOnClickListener {
            Log.d("Card","Status")
            mainActivityView = (activity as MainActivity)
            val bottomSheet = EditSettingsFragment(2)
            bottomSheet.show(mainActivityView.supportFragmentManager,"TAG")

        }
    }
    private fun deleteAccount(){
        binding.deleteCard.setOnClickListener {
            Log.d("Card","Delete")

        }
    }
    private fun observeLiveData(){
        viewModel.getUser(appUtil.getUID()!!).observe(viewLifecycleOwner, Observer {
            binding.userModel = it
            binding.settingsProgressBar.visibility = View.GONE
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings,container,false)
        observeLiveData()
        return binding.root
    }
    override fun onStop() {
        super.onStop()
        mainActivityView.activeState("offline")
    }
    override fun onDestroy() {
        super.onDestroy()
        mainActivityView.activeState("offline")
    }
    override fun onResume() {
        super.onResume()
        mainActivityView.activeState("online")
    }
}