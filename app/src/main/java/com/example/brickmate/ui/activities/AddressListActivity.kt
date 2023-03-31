package com.example.brickmate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.brickmate.R

class AddressListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)


    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }
}