package com.example.brickmate.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityPaymentBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Customer
import com.example.brickmate.ui.adapters.OrderListAdapter
import com.example.brickmate.ui.adapters.PaymentCustomerAdapter
import com.example.brickmate.ui.adapters.PaymentOrderAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class PaymentActivity : BaseActivity(), View.OnClickListener {
    private lateinit var customerSearchList: ArrayList<Customer>
    private lateinit var tempCustomerSearchList: ArrayList<Customer>
    private lateinit var binding: ActivityPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeVars()
        setUpActionBar()

        binding.btnPaymentGoToOrders.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        getOrderList()
    }

    private fun getOrderList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getCustomerListWithUnpaidOrdersActivity(this)
    }

    private fun initializeVars() {
        customerSearchList = ArrayList()
        tempCustomerSearchList = ArrayList()
    }

    private fun queryListener(query: String?) {
        tempCustomerSearchList.clear()
        val searchText = query!!.lowercase(Locale.getDefault())
        if (searchText.isNotEmpty()) {
            binding.rvPaymentSearchCustomer.visibility = View.VISIBLE
            binding.tvPaymentNoResultsFound.visibility = View.GONE
            customerSearchList.forEach {
                if (it.name.lowercase(Locale.getDefault())
                        .contains(searchText) || it.phone.contains(searchText)
                ) {
                    tempCustomerSearchList.add(it)
                }
            }
            binding.rvPaymentSearchCustomer.layoutManager =
                LinearLayoutManager(this@PaymentActivity)
            binding.rvPaymentSearchCustomer.setHasFixedSize(true)
            val adapter = PaymentCustomerAdapter(this@PaymentActivity, tempCustomerSearchList)
            binding.rvPaymentSearchCustomer.adapter = adapter
            if (tempCustomerSearchList.isEmpty()) {
                binding.rvPaymentSearchCustomer.visibility = View.GONE
                binding.tvPaymentNoResultsFound.visibility = View.VISIBLE
                binding.llNoOrdersMade.visibility = View.GONE
            } else {
                binding.rvPaymentSearchCustomer.visibility = View.VISIBLE
                binding.tvPaymentNoResultsFound.visibility = View.GONE
                binding.llNoOrdersMade.visibility = View.GONE
            }
        } else {
            binding.rvPaymentSearchCustomer.visibility = View.VISIBLE
            binding.tvPaymentNoResultsFound.visibility = View.GONE
            //successGetCustomerListFromFireStore(customerSearchList)
            successGetUnpaidCustomersList(customerSearchList)
        }
    }

    private fun setUpActionBar() {
        val toolbar = binding.toolbarPaymentActivity
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val ivIconBack = binding.icVectorBack
        ivIconBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val searchView = findViewById<SearchView>(R.id.search_view_order)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                queryListener(query)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                queryListener(query)
                return false
            }

        })
    }

    fun successGetUnpaidCustomersList(unpaidCustomers: MutableList<Customer>) {
        hideProgressDialog()
        if (unpaidCustomers.size > 0) {
            binding.rvPaymentSearchCustomer.visibility = View.VISIBLE
            binding.llNoOrdersMade.visibility = View.GONE
            customerSearchList = unpaidCustomers as ArrayList<Customer>
            val adapter = PaymentCustomerAdapter(this, customerSearchList)
            binding.rvPaymentSearchCustomer.layoutManager = LinearLayoutManager(this)
            binding.rvPaymentSearchCustomer.adapter = adapter
        } else {
            binding.rvPaymentSearchCustomer.visibility = View.GONE
            binding.llNoOrdersMade.visibility = View.VISIBLE
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_payment_go_to_orders -> {
                val intent = Intent(this, OrderActivity::class.java)
                startActivity(intent)
            }
        }
    }
}