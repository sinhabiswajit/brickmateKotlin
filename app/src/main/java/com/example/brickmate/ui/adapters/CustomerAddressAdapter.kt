package com.example.brickmate.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Address
import com.example.brickmate.util.GlideLoader

class CustomerAddressAdapter(
    private var context: Context,
    private var customerAddressList : ArrayList<Address>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddressViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_customer_addresses,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
       return customerAddressList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = customerAddressList[position]
        if (holder is AddressViewHolder){
            holder.address.text = model.address
            holder.zipcode.text = model.zipCode
            holder.landmark.text = model.landmark

        }
    }
}
private class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val address : TextView = view.findViewById(R.id.tv_customer_address)
    val zipcode : TextView = view.findViewById(R.id.tv_customer_zipcode)
    val landmark : TextView = view.findViewById(R.id.tv_customer_landmark)
}