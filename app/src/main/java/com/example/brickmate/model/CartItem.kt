package com.example.brickmate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem (
    val product_id : String = "",
    val title: String = "",
    val price: String = "",
    val uom : String = "",
    val image: String = "",
    var cart_quantity: String = "",
    var id: String = ""
        ) : Parcelable
