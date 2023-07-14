package com.example.brickmate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Payment(
    val date : String ="",
    val remarks : String="",
    val amount: Double = 0.0
) : Parcelable
