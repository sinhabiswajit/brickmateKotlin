package com.example.brickmate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Company(
    val user_id: String= "",
    val companyName : String = "",
    val companyLogo : String = "",
    val address : String = "",
    val city : String = "",
    val state : String = "",
    val pinCode : String = "",
    val added : Boolean = false
) : Parcelable
