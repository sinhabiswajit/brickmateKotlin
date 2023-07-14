package com.example.brickmate.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Address
import com.example.brickmate.ui.adapters.CustomerAddressAdapter
import com.example.brickmate.ui.adapters.ProductAdapter
import java.util.ArrayList

class AddressListActivity : BaseActivity() {
    private var toolBarAddressListActivity : Toolbar? = null
    private var tvAddNewAddress : TextView? = null
    private lateinit var customerName : String
    private lateinit var customerPhone : String
    private var tvNoAddressFound : TextView? = null
    private lateinit var rvAddressList : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        initializeVars()
        setUpActionBar()
        if (intent.hasExtra("name")){
            customerName = intent.getStringExtra("name").toString()
            customerPhone = intent.getStringExtra("phone").toString()
        }
        tvAddNewAddress?.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddAddressActivity::class.java)
            intent.putExtra("name", customerName)
            intent.putExtra("phone", customerPhone)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        getCustomerAddressListFromFireStore()
    }

    private fun getCustomerAddressListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getCustomerAddresses(this@AddressListActivity,customerName)
    }

    private fun initializeVars() {
        toolBarAddressListActivity = findViewById(R.id.toolbar_address_list_activity)
        tvAddNewAddress = findViewById(R.id.tv_add_address)
        tvNoAddressFound = findViewById(R.id.tv_no_address_found)
        rvAddressList = findViewById(R.id.rv_address_list)
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

    fun successGetCustomerAddressListFromFireStore(customerAddressList: ArrayList<Address>) {
        hideProgressDialog()
        if (customerAddressList.size > 0){
            tvNoAddressFound?.visibility = View.GONE
            rvAddressList.visibility = View.VISIBLE
            rvAddressList.layoutManager = LinearLayoutManager(this)
            rvAddressList.setHasFixedSize(true)
            val adapterCustomerAddressList = CustomerAddressAdapter(this@AddressListActivity, customerAddressList)
            rvAddressList.adapter = adapterCustomerAddressList
        }
        else{
            tvNoAddressFound?.visibility = View.VISIBLE
            rvAddressList.visibility = View.GONE
        }
    }
}