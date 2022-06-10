package com.dogukan.tellme.models

import android.os.Parcelable
import android.widget.ProgressBar
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dogukan.tellme.util.Addition
import com.dogukan.tellme.view.SettingsFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Users(
    @PrimaryKey(autoGenerate = false)
    var uid : String ,
    @ColumnInfo(name = "username")
    var username:String,
    @ColumnInfo(name = "profileImageURL")
    var profileImageURL:String,
    @ColumnInfo(name = "status")
    var status : String,
    @ColumnInfo(name = "Email")
    var Email : String,
    @ColumnInfo(name = "activeState")
    var activeState : String ,
    @ColumnInfo(name = "token")
    var token : String?) : Parcelable{

    constructor() : this("","","","","","offline","")

    companion object{
        private val addition = Addition()
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view : CircleImageView , imageUrl : String?){
            imageUrl?.let {
                addition.picassoUseIt(imageUrl,view)
            }
        }


    }
}