package com.example.brickmate.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.brickmate.R
import java.io.IOException

class GlideLoader(val context: Context)  {

    fun loadUserPicture(image: Any, imageView: ImageView){
        try {
            // Load the user image in the ImageView
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun loadProductPicture(image: Any, imageView: ImageView){
        try {
            // Load the user image in the ImageView
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_product_placeholder)
                .into(imageView)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
    fun loadCompanyLogo(image: Any, imageView: ImageView){
        try {
            // Load the user image in the ImageView
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_product_placeholder)
                .into(imageView)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

}