package com.example.brickmate.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Product
import com.example.brickmate.ui.adapters.OrderSummaryAdapter
import com.example.brickmate.ui.adapters.ProductAdapter

class OrderDetailDialogFragment : DialogFragment() {

    private lateinit var productList: ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productList = it.getSerializable("productList") as ArrayList<Product>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_order_detail_dialog, container, false)

        // Initialize the RecyclerView and its adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_dialog_products)
        val adapter = OrderSummaryAdapter(requireContext(), productList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    override fun onResume() {
        super.onResume()

        // Set the dialog width and height
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        fun newInstance(productList: ArrayList<Product>): OrderDetailDialogFragment {
            val args = Bundle()
            args.putSerializable("productList", productList)
            val fragment = OrderDetailDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}