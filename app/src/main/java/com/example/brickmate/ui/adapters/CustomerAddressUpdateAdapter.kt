package com.example.brickmate.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Address
import com.example.brickmate.ui.activities.CustomerDetailUpdateActivity

class CustomerAddressUpdateAdapter(
    private var context: Context,
    private var customerAddressList : ArrayList<Address>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Declare interface for delete button click listener
    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    private var onDeleteClickListener: OnDeleteClickListener? = null

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddressUpdateViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_customer_address_delete,
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
        if (holder is AddressUpdateViewHolder){
            holder.address.text = model.address
            holder.zipcode.text = model.zipCode
            holder.landmark.text = model.landmark
            holder.delete.setOnClickListener{
                onDeleteClickListener?.onDeleteClick(position)
            }
        }
    }
}
private class AddressUpdateViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val address : TextView = view.findViewById(R.id.tv_customer_address)
    val zipcode : TextView = view.findViewById(R.id.tv_customer_zipcode)
    val landmark : TextView = view.findViewById(R.id.tv_customer_landmark)
    val delete : ImageView = view.findViewById(R.id.iv_customer_address_delete)
}