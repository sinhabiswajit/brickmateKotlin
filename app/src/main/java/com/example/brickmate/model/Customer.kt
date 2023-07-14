package com.example.brickmate.model

import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Customer(
    val user_id: String = "",
    var customer_id: String = "",
    var name: String = "",
    var phone: String = "",
    var email: String = "",
    var gstin: String = "",
    var addresses: List<Address> = emptyList(),
    var siteContactPerson: String = "",
    var orders: List<Order> = emptyList()
) : Parcelable
