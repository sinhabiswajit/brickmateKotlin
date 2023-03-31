package com.example.brickmate.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Customer
import com.example.brickmate.ui.adapters.CustomerAdapter

class CustomerActivity : BaseActivity() {

    private var toolBarCustomerActivity : Toolbar? = null
    private lateinit var rvCustomerList : RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        initializeVars()
        setUpActionBar()
        getCustomerList()

    }

    private fun getCustomerList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getCustomerListFromFireStore(this@CustomerActivity)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.customer_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_add_customer -> {
                val intent = Intent(this@CustomerActivity, AddCustomerActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeVars() {
        toolBarCustomerActivity = findViewById(R.id.toolbar_customer_activity)
        rvCustomerList = findViewById(R.id.rv_customer_list)
    }
    private fun setUpActionBar() {
        setSupportActionBar(toolBarCustomerActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolBarCustomerActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun successGetCustomerListFromFireStore(customerList: ArrayList<Customer>) {
        hideProgressDialog()
        //Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show()
        if (customerList.size > 0){
            rvCustomerList.layoutManager = LinearLayoutManager(this)
            rvCustomerList.setHasFixedSize(true)
            val adapter = CustomerAdapter(this@CustomerActivity, customerList)
            rvCustomerList.adapter = adapter
        }
    }
}