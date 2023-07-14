package com.example.brickmate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val user_id: String= "",
    var product_id: String = "",
    val name: String = "",
    val description: String = "",
    val uom: String = "",
    val sell_price: String = "",
    var gst_rate: String = "",
    val product_image: String = "",
    var quantity: Int = 0,
    var isSelected: Boolean = false,
    var gstIncluded: Boolean = false
) : Parcelable
