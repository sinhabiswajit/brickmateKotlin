package com.example.brickmate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val address: String = "",
    val zipCode: String = "",
    val landmark: String = ""
) : Parcelable
