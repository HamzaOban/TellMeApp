package com.dogukan.tellme.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Users(val uid : String , val username:String,val profileImageURL:String) : Parcelable{
    constructor() : this("","","")
}