package com.example.brickmate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Quotation(
    var quotation_id : String = "",
    val quotation_date : String = "",
    val quotation_valid_date : String = "",
    val name : String = "",
    val phone : String = "",
    val location : String = "",
    val additional_info : String = "",
    var productList: List<Product> = listOf()
) : Parcelable
