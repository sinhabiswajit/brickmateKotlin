package com.example.brickmate.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.util.Constants

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(Constants.BRICKMATE_PREFERENCES, Context.MODE_PRIVATE)
        val status = sharedPreferences.getBoolean(Constants.LOG_IN_STATUS,false)

        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                if (status){
                    startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                }else{
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                }
                finish()

//                val currentUserID = FireStoreClass().getCurrentUserID()
//
//                if (currentUserID.isNotEmpty()){
//                    startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
//                }else{
//                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//                }
//                finish()
            },2000
        )
    }

}