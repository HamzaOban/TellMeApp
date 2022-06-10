package com.dogukan.tellme.view

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.dogukan.tellme.databinding.FragmentReViewSendImageBinding
import com.dogukan.tellme.viewmodel.ChatViewModel

class ReViewSendImageFragment : Fragment() {
    private lateinit var binding : FragmentReViewSendImageBinding
    private val viewModel : ChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReViewSendImageBinding.inflate(layoutInflater)
        arguments.let { it ->
            val uriString = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).imageURL }
            val toID = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).toID }
            val activeState = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).activeState }
            val email = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).email }
            val position = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).position }
            val status = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).status }
            val username = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).username }
            val token = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).token }

            if (uriString!=null){
                val uri = Uri.parse(uriString) ?: return@let
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
                val bitmapDrawable = BitmapDrawable(bitmap)
                binding.sendImageView.setImageDrawable(bitmapDrawable)
                binding.SendBtn.setOnClickListener {
                    viewModel.sendImage(true)
                    if (toID != null) {
                        viewModel.performSendImage(uri, toID)
                    }
                    val action = ReViewSendImageFragmentDirections.actionReViewSendImageFragmentToChatLogFragment(position!!,toID!!,username!!,uriString,status!!,activeState!!,email!!,token!!)
                    view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }

                }

            }
        }

        return binding.root
    }

}