package com.example.brickmate.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Customer
import com.example.brickmate.ui.activities.CustomerActivity
import com.example.brickmate.ui.activities.CustomerDetailActivity

class CustomerAdapter(
    private val context: Context,
    private var list : ArrayList<Customer>,
    private val activity: CustomerActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CustomerViewHolder(
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
        if (holder is CustomerViewHolder){
            holder.name.text = model.name
            holder.phone.text = model.phone
            holder.itemView.setOnClickListener{
                //Toast.makeText(context, "${model.name} clicked!", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, CustomerDetailActivity::class.java)
                intent.putExtra("customer_model", model)
                context.startActivity(intent)
            }
//            holder.email.text = model.email
//            holder.address.setOnClickListener {
//                val intent = Intent(context, AddressListActivity::class.java)
//                intent.putExtra("name", model.name)
//                intent.putExtra("phone", model.phone)
//                context.startActivity(intent)
//            }
//            holder.update.setOnClickListener {
//                Log.e("CustomerID", model.customer_id)
//            }
//            holder.delete.setOnClickListener {
//                activity.deleteCustomer(model.customer_id)
//            }
//            holder.orders.setOnClickListener {
//                val intent = Intent(context, OrderByCustomer::class.java)
//                intent.putExtra("phone", model.phone)
//                context.startActivity(intent)
//            }
        }
    }
}
    private class CustomerViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val name : TextView = view.findViewById(R.id.tv_customer_name)
        val phone : TextView = view.findViewById(R.id.tv_customer_phone)
    }
