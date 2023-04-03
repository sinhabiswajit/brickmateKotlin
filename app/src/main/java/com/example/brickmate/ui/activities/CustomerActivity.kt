package com.example.brickmate.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Customer
import com.example.brickmate.ui.adapters.CustomerAdapter
import java.util.*
import kotlin.collections.ArrayList

class CustomerActivity : BaseActivity() {

    private var toolBarCustomerActivity : Toolbar? = null
    private lateinit var rvCustomerList : RecyclerView
    private lateinit var tempCustomerArrayList: ArrayList<Customer>
    private lateinit var customerArrayList : ArrayList<Customer>


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
            R.id.action_search_customer -> {
                val searchView = item.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        TODO("Not yet implemented")
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        tempCustomerArrayList.clear()
                        val searchText = newText!!.lowercase(Locale.getDefault())
                        if (searchText.isNotEmpty()){
                            customerArrayList.forEach {
                                if (it.name.lowercase(Locale.getDefault()).contains(searchText) || it.phone.contains(searchText)){
                                    tempCustomerArrayList.add(it)
                                }
                            }
                            rvCustomerList.layoutManager = LinearLayoutManager(this@CustomerActivity)
                            rvCustomerList.setHasFixedSize(true)
                            val adapter = CustomerAdapter(this@CustomerActivity, tempCustomerArrayList)
                            rvCustomerList.adapter = adapter
                        }
                        else{
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
        toolBarCustomerActivity = findViewById(R.id.toolbar_customer_activity)
        rvCustomerList = findViewById(R.id.rv_customer_list)
        customerArrayList = ArrayList()
        tempCustomerArrayList = ArrayList()
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
        customerArrayList = customerList
        //Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show()
        if (customerList.size > 0){
            rvCustomerList.layoutManager = LinearLayoutManager(this)
            rvCustomerList.setHasFixedSize(true)
            val adapter = CustomerAdapter(this@CustomerActivity, customerList)
            rvCustomerList.adapter = adapter
        }
    }
}