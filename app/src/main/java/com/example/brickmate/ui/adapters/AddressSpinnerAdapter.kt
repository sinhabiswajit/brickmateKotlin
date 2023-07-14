package com.example.brickmate.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.brickmate.R
import com.example.brickmate.model.Address


class AddressSpinnerAdapter(
    private val context: Context,
    private val addresses: List<Address>
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_spinner_address, parent, false)

        val address = addresses[position]

        // bind data to views in the custom layout
        val tvAddress = view.findViewById<TextView>(R.id.tv_customer_spinner_item_address)
        tvAddress.text = address.address
        val tvZipCode = view.findViewById<TextView>(R.id.tv_customer_spinner_item_zipcode)
        tvZipCode.text = address.zipCode
        val tvLandmark = view.findViewById<TextView>(R.id.tv_customer_spinner_item_landmark)
        tvLandmark.text = address.landmark

        return view
    }
    override fun getCount(): Int {
        return addresses.size
    }

    override fun getItem(position: Int): Any {
        return addresses[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}