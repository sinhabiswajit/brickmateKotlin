package com.example.brickmate.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityCustomerBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Customer
import com.example.brickmate.model.Product
import com.example.brickmate.ui.adapters.CustomerAdapter
import java.util.*
import kotlin.collections.ArrayList

class CustomerActivity : BaseActivity(), View.OnClickListener {

    private var binding: ActivityCustomerBinding? = null
    private lateinit var rvCustomerList: RecyclerView
    private lateinit var tempCustomerArrayList: ArrayList<Customer>
    private lateinit var customerArrayList: ArrayList<Customer>
    private lateinit var btnAddNewCustomer : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initializeVars()
        setUpActionBar()
        btnAddNewCustomer.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
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
            R.id.action_search_customer -> {
                val searchView = item.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        TODO("Not yet implemented")
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        tempCustomerArrayList.clear()
                        val searchText = newText!!.lowercase(Locale.getDefault())
                        if (searchText.isNotEmpty()) {
                            customerArrayList.forEach {
                                if (it.name.lowercase(Locale.getDefault())
                                        .contains(searchText) || it.phone.contains(searchText)
                                ) {
                                    tempCustomerArrayList.add(it)
                                }
                            }
                            rvCustomerList.layoutManager =
                                LinearLayoutManager(this@CustomerActivity)
                            rvCustomerList.setHasFixedSize(true)
                            val adapter = CustomerAdapter(
                                this@CustomerActivity,
                                tempCustomerArrayList,
                                this@CustomerActivity
                            )
                            rvCustomerList.adapter = adapter
                        } else {
                            successGetCustomerListFromFireStore(customerArrayList)
                        }
                        return false
                    }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeVars() {
        customerArrayList = ArrayList()
        tempCustomerArrayList = ArrayList()
        rvCustomerList = findViewById(R.id.rv_customer_list)
        btnAddNewCustomer = findViewById(R.id.btn_add_new_customer)
    }

    private fun setUpActionBar() {
        val toolbar = binding?.toolbarCustomerActivity
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolbar?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun sortCustomersAlphabetically(customerList: ArrayList<Customer>) {
        customerList.sortBy { it.name }
    }

    fun successGetCustomerListFromFireStore(customerList: ArrayList<Customer>) {
        hideProgressDialog()
        customerArrayList = customerList
        //Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show()
        if (customerList.size > 0) {
            binding?.llNoCustomerFound?.visibility = View.GONE
            binding?.rvCustomerList?.visibility = View.VISIBLE
            binding?.rvCustomerList?.layoutManager = LinearLayoutManager(this)
            binding?.rvCustomerList?.setHasFixedSize(true)
            sortCustomersAlphabetically(customerList)
            val adapter =
                CustomerAdapter(this@CustomerActivity, customerList, this@CustomerActivity)
            binding?.rvCustomerList?.adapter = adapter
        } else {
            binding?.llNoCustomerFound?.visibility = View.VISIBLE
            binding?.rvCustomerList?.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_add_new_customer -> {
                val intent = Intent(this@CustomerActivity, AddCustomerActivity::class.java)
                startActivity(intent)
            }
        }
    }
}