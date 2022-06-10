package com.dogukan.tellme.view

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.dogukan.tellme.R
import com.dogukan.tellme.databinding.FragmentEditSettingsBinding
import com.dogukan.tellme.util.AppUtil
import com.dogukan.tellme.viewmodel.SettingsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditSettingsFragment(var Which : Int) : BottomSheetDialogFragment() {

    private val viewModel : SettingsViewModel by viewModels()
    private lateinit var  mainActivityView : MainActivity
    private  var AppUtil = AppUtil()

    private lateinit var binding : FragmentEditSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)

    }

    override fun onStart() {

        super.onStart()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditSettingsBinding.inflate(layoutInflater)
        binding.CancelButton.setOnClickListener{
            this.dismiss()
        }

        if (Which==1){
            binding.ChangeTextView.setText(R.string.Change_name)
            binding.changedNameTV.setText(viewModel.getUser(AppUtil.getUID()!!).value?.username)
            binding.changedNameTV.filters += InputFilter.LengthFilter(20)
            binding.SaveButton.setOnClickListener{

                val userName = binding.changedNameTV.text.toString()
                viewModel.changeUserName(userName)
                viewModel.getUser(AppUtil.getUID()!!)
                this.dismiss()
            }
            Log.d("Which", "1")
        }
        else if (Which==2){
            binding.ChangeTextView.setText(R.string.Change_status)
            binding.changedNameTV.setText(viewModel.getUser(AppUtil.getUID()!!).value?.status)
            binding.changedNameTV.filters += InputFilter.LengthFilter(118)
            binding.SaveButton.setOnClickListener{
                val status = binding.changedNameTV.text.toString()
                viewModel.changeStatus(status)
                viewModel.getUser(AppUtil.getUID()!!)
                this.dismiss()
            }
            Log.d("Which", "2")
        }

        // Inflate the layout for this fragment
        return binding.root
    }



}