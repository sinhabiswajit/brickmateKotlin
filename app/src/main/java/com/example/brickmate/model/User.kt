package com.example.brickmate.model

import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
class User (
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = ""
    ) : Parcelable