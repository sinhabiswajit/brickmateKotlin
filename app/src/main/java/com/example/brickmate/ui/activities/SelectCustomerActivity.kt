package com.example.brickmate.ui.activities

import androidx.appcompat.app.AppCompatActivity
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
import com.example.brickmate.ui.adapters.SearchCustomerAdapter
import java.util.*

class SelectCustomerActivity : BaseActivity() {
    private var toolBarSelectCustomerActivity : Toolbar? = null
    private lateinit var rvSearchCustomer : RecyclerView
    private lateinit var customerSearchList : ArrayList<Customer>
    private lateinit var tempCustomerSearchList: ArrayList<Customer>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_customer)

        initializeVars()
        setSupportActionBar(toolBarSelectCustomerActivity)
        getCustomerList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.order_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_search_order_customer -> {
                val searchView = item.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        queryListener(query)
                        return false
                    }
                    override fun onQueryTextChange(newText: String?): Boolean {
                        queryListener(newText)
                        return false
                    }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun queryListener(newText : String?) {
        tempCustomerSearchList.clear()
        val searchText = newText!!.lowercase(Locale.getDefault())
        if (searchText.isNotEmpty()){
            customerSearchList.forEach {
                if (it.name.lowercase(Locale.getDefault()).contains(searchText) || it.phone.contains(searchText)){
                    tempCustomerSearchList.add(it)
                }
            }
            rvSearchCustomer.layoutManager = LinearLayoutManager(this@SelectCustomerActivity)
            rvSearchCustomer.setHasFixedSize(true)
            val adapter = SearchCustomerAdapter(this@SelectCustomerActivity, tempCustomerSearchList,this)
            rvSearchCustomer.adapter = adapter
        }
    }

    private fun initializeVars() {
        toolBarSelectCustomerActivity = findViewById(R.id.toolbar_select_customer_activity)
        rvSearchCustomer = findViewById(R.id.rv_search_customer)
        tempCustomerSearchList = ArrayList()
        customerSearchList = ArrayList()
    }

    private fun getCustomerList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getCustomerListFromFireStore(this@SelectCustomerActivity)
    }

    fun successGetCustomerListFromFireStore(searchCustomerList: ArrayList<Customer>) {
        hideProgressDialog()
        customerSearchList = searchCustomerList
    }
}