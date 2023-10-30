package com.example.brickmate.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val REQUEST_CODE_UPDATE_CUSTOMER: Int = 10
    const val EMAIL: String = "email"
    const val GOOGLE: String = "google"
    const val FACEBOOK: String = "facebook"
    const val TWITTER: String = "twitter"
    const val LOGGED_IN_WITH: String = "loggedInWith"
    const val QUOTATION: String = "quotation"
    const val ORDERS: String = "orders"
    const val CART_ITEMS: String = "cart_items"
    const val DEFAULT_PRODUCT_QUANTITY: String = "1"
    const val CART_QUANTITY: String = "cart_quantity"
    const val ADDRESSES: String = "addresses"
    const val CUSTOMER_DETAILS: String = "customer_details"
    const val CUSTOMER_ADDRESS_LIST: String = "customer_address_list"
    const val USERS: String = "users"
    const val USER_ID: String = "user_id"
    const val CUSTOMERS: String = "customers"
    const val PRODUCTS : String = "products"
    const val COMPANY : String = "company"
    const val EXTRA_PRODUCT_ID: String = "extra_product_id"

    const val BRICKMATE_PREFERENCES: String = "BrickMatePrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val LOG_IN_STATUS : String = "login_status"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"

    const val READ_STORAGE_PERMISSION_CODE: Int = 2
    const val PICK_IMAGE_REQUEST_CODE : Int = 1
    const val RC_SIGN_IN : Int = 5

    const val PRODUCT_IMAGE : String = "Product_image"
    const val COMPANY_LOGO : String = "Company_logo"

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?) : String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}