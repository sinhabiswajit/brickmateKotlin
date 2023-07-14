package com.example.brickmate.ui.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.FragmentNewOrdersBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Customer
import com.example.brickmate.ui.adapters.OrderListAdapter


class NewOrdersFragment : BaseFragment() {

    private var _binding: FragmentNewOrdersBinding? = null
    private val binding get() = _binding!!
    private var unpaidCustomers: ArrayList<Customer> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_new_orders, container, false)
        _binding = FragmentNewOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onResume() {
        super.onResume()
        getUnpaidOrders()
    }

    private fun getUnpaidOrders() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getCustomerListWithUnpaidOrders(this)
    }

    fun searchOrders(query: String?) {
        val filteredList = this.unpaidCustomers.filter { customer ->
            customer.name.contains(query ?: "", ignoreCase = true) || customer.phone.contains(query ?: "", ignoreCase = true)
        }
        updateRecyclerView(filteredList as ArrayList<Customer>)
    }

    fun updateUnpaidCustomersList(unpaidCustomers: MutableList<Customer>) {
        hideProgressDialog()
        this.unpaidCustomers = unpaidCustomers as ArrayList<Customer>
        Log.d("Customers", unpaidCustomers.toString())
        for (customer in this.unpaidCustomers) {
            Log.d(TAG, "CustomerId: ${customer.customer_id} Name: ${customer.name}, Phone: ${customer.phone}")
        }
        if (unpaidCustomers.size > 0){
            updateRecyclerView(this.unpaidCustomers)
        }else{
            binding.rvUnpaidCustomerOrders.visibility = View.GONE
            binding.llNoOrdersFound.visibility = View.VISIBLE
        }

    }
    private fun sortCustomersAlphabetically(customerList: ArrayList<Customer>) {
        customerList.sortBy { it.name }
    }

    private fun updateRecyclerView(unpaidCustomers: ArrayList<Customer>) {
        val customers = unpaidCustomers
        sortCustomersAlphabetically(unpaidCustomers)
        binding.rvUnpaidCustomerOrders.visibility = View.VISIBLE
        binding.llNoOrdersFound.visibility = View.GONE
        binding.rvUnpaidCustomerOrders.layoutManager = LinearLayoutManager(context)
        val adapter = OrderListAdapter(requireContext(), customers)
        binding.rvUnpaidCustomerOrders.adapter = adapter
    }
}