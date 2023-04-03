package com.example.brickmate.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
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

class SearchCustomerAdapter(
    private var context: Context,
    private var searchCustomerList : ArrayList<Customer>,
    private var activity: SelectCustomerActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchCustomerViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_search_customer,
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
            holder.clSearchCustomer.setOnClickListener {
                val intent = Intent(context.applicationContext, OrderActivity::class.java)
                intent.putExtra("name", model.name)
                intent.putExtra("phone", model.phone)
                //Toast.makeText(context,"Clicked ${model.name} and phone ${model.phone}", Toast.LENGTH_SHORT).show()
                context.startActivity(intent)
                activity.finish()
            }
        }

    }
}
private class SearchCustomerViewHolder(view : View) : RecyclerView.ViewHolder(view){
    val name : TextView = view.findViewById(R.id.tv_search_customer_name)
    val phone : TextView = view.findViewById(R.id.tv_search_customer_number)
    val clSearchCustomer : ConstraintLayout = view.findViewById(R.id.cl_search_customer)

}