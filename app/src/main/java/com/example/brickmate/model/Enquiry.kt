package com.example.brickmate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Enquiry(
    var enquiry_id : String = "",
    val enquiry_date : String = "",
    val name : String = "",
    val phone : String = "",
    val location : String = "",
    val additional_info : String = "",
    var productList: List<Product> = listOf()
) : Parcelable
