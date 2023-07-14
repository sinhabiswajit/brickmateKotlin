package com.example.brickmate.ui.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.brickmate.R
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private lateinit var mProgressDialog: Dialog

//    fun showErrorSnackBar(message: String, errorMessage: Boolean){
//        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
//        val snackBarView = snackBar.view
//
//        if (errorMessage){
//            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.color_snack_bar_error))
//
//        }else{
//            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.color_snack_bar_success))
//        }
//        snackBar.show()
//    }

    fun generateOrderId(): String {
        val date = SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("HHmmss", Locale.getDefault()).format(Date())
        return "ORDER-$date-$time"
    }

    fun generateOrderDate(): String {
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        return "$date - $time"
    }

    fun showProgressDialog(text: String) {
        if (!::mProgressDialog.isInitialized || !mProgressDialog.isShowing) {
            mProgressDialog = Dialog(this)
            mProgressDialog.setContentView(R.layout.dialog_progress)
            mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text
            mProgressDialog.setCancelable(false)
            mProgressDialog.setCanceledOnTouchOutside(false)
            mProgressDialog.show()
        }
    }

    fun hideProgressDialog() {
        if (::mProgressDialog.isInitialized && mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_LONG
        ).show()
        @Suppress("DEPRECATION")
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}