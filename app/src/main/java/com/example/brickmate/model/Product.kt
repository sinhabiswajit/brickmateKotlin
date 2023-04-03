package com.example.brickmate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product (
    var product_id : String = "",
    val name : String = "",
    val description : String = "",
    val uom : String = "",
    val sell_price : String = "",
    val gst_rate : String = "",
    val product_image : String = ""
) : Parcelable
