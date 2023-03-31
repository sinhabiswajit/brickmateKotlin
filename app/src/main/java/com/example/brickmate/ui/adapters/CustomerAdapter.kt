package com.example.brickmate.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Customer
import com.example.brickmate.ui.activities.AddressListActivity
import com.example.brickmate.util.Constants

class CustomerAdapter(
    private val context: Context,
    private var list : ArrayList<Customer>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
            R.layout.item_customer,
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder){
            holder.name.text = model.name
            holder.phone.text = model.phone
            holder.email.text = model.email
            holder.address.setOnClickListener {
                Toast.makeText(context,"Addresses", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, AddressListActivity::class.java)
                intent.putExtra(Constants.CUSTOMER_ADDRESS_LIST, model)
                context.startActivity(intent)
            }
        }
    }
}
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val name : TextView = view.findViewById(R.id.tv_customer_name)
        val phone : TextView = view.findViewById(R.id.tv_customer_phone)
        val email : TextView = view.findViewById(R.id.tv_customer_email)
        val address : LinearLayout = view.findViewById(R.id.ll_customer_addresses)
    }
