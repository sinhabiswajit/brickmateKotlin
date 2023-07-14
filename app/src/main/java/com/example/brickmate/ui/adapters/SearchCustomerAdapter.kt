package com.example.brickmate.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Customer
import com.example.brickmate.ui.activities.OrderActivity
import com.example.brickmate.ui.activities.SelectCustomerActivity
import com.example.brickmate.util.Constants
import com.example.brickmate.util.CustomerClickInterface

class SearchCustomerAdapter(
    private var context: Context,
    private var searchCustomerList : ArrayList<Customer>,
    private val customerClickInterface: CustomerClickInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchCustomerViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_customer_make_order,
                parent,
                false
            ))
    }
    override fun getItemCount(): Int {
        return searchCustomerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = searchCustomerList[position]
        if (holder is SearchCustomerViewHolder){
            holder.name.text = model.name
            holder.phone.text = model.phone
            holder.itemView.setOnClickListener {
                //Toast.makeText(context,"Item clicked", Toast.LENGTH_SHORT).show()
                customerClickInterface.onItemClick(model)
            }

//            holder.clSearchCustomer.setOnClickListener {
//                val intent = Intent(context, OrderActivity::class.java)
//                intent.putExtra("customer_id", model.customer_id)
//                intent.putExtra("name", model.name)
//                intent.putExtra("phone", model.phone)
//                intent.putExtra("customer_model", model)
//                context.startActivity(intent)
//                activity.finish()
//            }
        }

    }
}
private class SearchCustomerViewHolder(view : View) : RecyclerView.ViewHolder(view){
    val name : TextView = view.findViewById(R.id.tv_customer_name)
    val phone : TextView = view.findViewById(R.id.tv_customer_phone)
    //val clSearchCustomer : ConstraintLayout = view.findViewById(R.id.cl_search_customer)

}
