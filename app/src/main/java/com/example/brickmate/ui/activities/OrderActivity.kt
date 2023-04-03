package com.example.brickmate.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Customer
import com.example.brickmate.ui.adapters.CustomerAdapter
import com.example.brickmate.ui.adapters.SearchCustomerAdapter
import com.example.brickmate.util.Constants
import java.util.*
import kotlin.collections.ArrayList

class OrderActivity : BaseActivity() {
    private var toolBarOrderActivity : Toolbar? = null
    private var btnAddCustomer : Button? = null
    private var btnAddProduct: Button? = null
    private var tvOrderCustomerName : TextView? = null
    private var tvOrderCustomerPhone : TextView? = null
    private var llOrderCustomerDetail : LinearLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        initializeVars()
        setUpActionBar()
        btnAddCustomer?.setOnClickListener {
            val intent = Intent(this, SelectCustomerActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (intent.hasExtra("name")){
            llOrderCustomerDetail?.visibility = View.VISIBLE
            btnAddCustomer?.visibility = View.GONE
            btnAddProduct?.visibility = View.VISIBLE
            tvOrderCustomerName?.text = intent.getStringExtra("name")
            tvOrderCustomerPhone?.text = intent.getStringExtra("phone")
        }

    }
    private fun initializeVars() {
        toolBarOrderActivity = findViewById(R.id.toolbar_order_activity)
        btnAddCustomer = findViewById(R.id.btn_order_add_customer)
        btnAddProduct = findViewById(R.id.btn_order_add_product)
        llOrderCustomerDetail = findViewById(R.id.ll_order_customer_detail)
        tvOrderCustomerName = findViewById(R.id.tv_order_customer_name)
        tvOrderCustomerPhone = findViewById(R.id.tv_order_customer_phone)
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolBarOrderActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolBarOrderActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}