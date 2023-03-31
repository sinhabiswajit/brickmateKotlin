package com.example.brickmate.model

import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Customer (
    var customer_id : String = "",
    val name : String = "",
    val phone : String = "",
    val email : String = ""
) : Parcelable
