package com.example.brickmate.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.brickmate.R

class AddressListActivity : AppCompatActivity() {
    private var toolBarAddressListActivity : Toolbar? = null
    private var tvAddNewAddress : TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        initializeVars()
        setUpActionBar()
        tvAddNewAddress?.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddAddressActivity::class.java)
            startActivity(intent)
        }

    }

    private fun initializeVars() {
        toolBarAddressListActivity = findViewById(R.id.toolbar_address_list_activity)
        tvAddNewAddress = findViewById(R.id.tv_add_address)
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolBarAddressListActivity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolBarAddressListActivity?.setNavigationOnClickListener { onBackPressed() }
    }
}