package com.example.brickmate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    val user_id: String = "",
    val orderId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val phone: String = "",
    val address: String = "",
    val productList: List<Product> = emptyList(),
    val total: Double = 0.0,
    val paid: Boolean = false,
    val orderDate: String = "",
    val delivered: Boolean = false,
    val deliveryDate : String = "",
    val paymentList: ArrayList<Payment> =  ArrayList()
) : Parcelable
