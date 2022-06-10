package com.dogukan.tellme.util

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.helper.widget.MotionPlaceholder
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.dogukan.tellme.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class Addition{
    fun picassoUseIt(uri: Uri,imageView: ImageView){
        Picasso.get().load(uri).into(imageView)

    }
    fun picassoUseIt(uri: Uri,imageView: CircleImageView){
        Picasso.get().load(uri).into(imageView)
    }
    fun picassoUseIt(url: String?=null,imageView: ImageView?=null){
        Picasso.get().load(url).into(imageView)


    }
    fun picassoUseIt(url: String?=null,imageView: ImageView?=null,progressBar: ProgressBar){
        Picasso.get().load(url).into(imageView, object: com.squareup.picasso.Callback {
            override fun onSuccess() {
                progressBar.visibility = View.GONE
                //set animations here

            }

            override fun onError(e: java.lang.Exception?) {
                progressBar.visibility = View.VISIBLE
            }
        })


    }
}
