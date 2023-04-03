package com.example.brickmate.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val CUSTOMER_DETAILS: String = "customer_details"
    const val CUSTOMER_ADDRESS_LIST: String = "customer_address_list"
    const val USERS: String = "users"
    const val CUSTOMERS: String = "customers"
    const val PRODUCTS : String = "products"

    const val BRICKMATE_PREFERENCES: String = "BrickMatePrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val LOG_IN_STATUS : String = "login_status"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"

    const val READ_STORAGE_PERMISSION_CODE: Int = 2
    const val PICK_IMAGE_REQUEST_CODE : Int = 1

    const val PRODUCT_IMAGE : String = "Product_image"

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?) : String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}