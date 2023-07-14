package com.example.brickmate.ui.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.FragmentPaidOrderBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Customer
import com.example.brickmate.ui.adapters.OrderListAdapter
import java.util.ArrayList

class PaidOrderFragment : BaseFragment() {

    private var _binding : FragmentPaidOrderBinding? = null
    private val binding get() = _binding!!
    private var paidCustomers: ArrayList<Customer> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_unpaid_order_list, container, false)
        _binding = FragmentPaidOrderBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getPaidOrders()
    }

    private fun getPaidOrders() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getCustomerListWithPaidOrders(this, true)
    }

    fun updatePaidCustomersList(customerMutableList: MutableList<Customer>) {
        hideProgressDialog()
        this.paidCustomers = customerMutableList as ArrayList<Customer>
        Log.d("Customers", paidCustomers.toString())
        for (customer in this.paidCustomers) {
            Log.d(ContentValues.TAG, "CustomerId: ${customer.customer_id} Name: ${customer.name}, Phone: ${customer.phone}")
        }
        if (paidCustomers.size > 0){
            updateRecyclerView(this.paidCustomers)
        }else{
            binding.rvPaidOrders.visibility = View.GONE
            binding.llNoOrdersFound.visibility = View.VISIBLE
        }
    }

    private fun sortCustomersAlphabetically(customerList: ArrayList<Customer>) {
        customerList.sortBy { it.name }
    }

    private fun updateRecyclerView(paidCustomers: ArrayList<Customer>) {
        val customers = paidCustomers
        sortCustomersAlphabetically(paidCustomers)
        binding.rvPaidOrders.visibility = View.VISIBLE
        binding.llNoOrdersFound.visibility = View.GONE
        binding.rvPaidOrders.layoutManager = LinearLayoutManager(context)
        val adapter = OrderListAdapter(requireContext(), customers)
        binding.rvPaidOrders.adapter = adapter
    }

    fun searchOrders(query: String?) {
        val filteredList = this.paidCustomers.filter { customer ->
            customer.name.contains(query ?: "", ignoreCase = true) || customer.phone.contains(query ?: "", ignoreCase = true)
        }
        updateRecyclerView(filteredList as ArrayList<Customer>)
    }


}