package com.example.brickmate.ui.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.FragmentAllOrdersBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Customer
import com.example.brickmate.model.Order
import com.example.brickmate.ui.adapters.OrderListAdapter
import java.util.ArrayList

class AllOrdersFragment : BaseFragment() {

    private var _binding : FragmentAllOrdersBinding? = null
    private val binding get() = _binding!!
    private var allCustomers: ArrayList<Customer> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_past_orders, container, false)
        _binding = FragmentAllOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getAllOrders()
    }

    private fun getAllOrders() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getCustomerListWithAllOrders(this, true)
    }

    fun updateAllCustomersList(allCustomers: MutableList<Customer>) {
        hideProgressDialog()
        this.allCustomers = allCustomers as ArrayList<Customer>
        Log.d("Customers", allCustomers.toString())
        for (customer in this.allCustomers) {
            Log.d(ContentValues.TAG, "CustomerId: ${customer.customer_id} Name: ${customer.name}, Phone: ${customer.phone}")
        }
        if (this.allCustomers.size > 0){
            updateRecyclerView(this.allCustomers)
        }else{
            binding.rvPastOrders.visibility = View.GONE
            binding.llNoOrdersFound.visibility = View.VISIBLE
        }
    }
    private fun sortCustomersAlphabetically(customerList: ArrayList<Customer>) {
        customerList.sortBy { it.name }
    }

    private fun updateRecyclerView(allCustomers: ArrayList<Customer>) {
        val customers = allCustomers
        sortCustomersAlphabetically(allCustomers)
        binding.rvPastOrders.visibility = View.VISIBLE
        binding.llNoOrdersFound.visibility = View.GONE
        binding.rvPastOrders.layoutManager = LinearLayoutManager(context)
        val adapter = OrderListAdapter(requireContext(), customers)
        binding.rvPastOrders.adapter = adapter
    }

    fun searchOrders(query: String?) {
        val filteredList = this.allCustomers.filter { customer ->
            customer.name.contains(query ?: "", ignoreCase = true) || customer.phone.contains(query ?: "", ignoreCase = true)
        }
        updateRecyclerView(filteredList as ArrayList<Customer>)
    }

}