package com.example.brickmate.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Customer
import com.example.brickmate.model.Order
import com.example.brickmate.util.Constants
import com.google.firebase.firestore.FirebaseFirestore

class PaymentCustomerAdapter(
    private val context: Context,
    private val customerList : ArrayList<Customer>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isExpanded = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PaymentCustomerViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_payment_customer,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return customerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = customerList[position]
        if (holder is PaymentCustomerViewHolder) {
            holder.name.text = model.name
            holder.phone.text = model.phone

            holder.itemView.setOnClickListener {
                isExpanded = !isExpanded
                if (isExpanded) {
                    holder.expandIcon.setImageResource(R.drawable.ic_vector_arrow_circle_up)
                    //val customerOrders = orders.filter { it.customerName == model.name }
                    holder.rvOrders.layoutManager = LinearLayoutManager(context)
                    val adapter = PaymentOrderAdapter(context, customerList[position].orders as ArrayList<Order>)
                    Log.d("orders", customerList[position].orders.toString())
                    holder.rvOrders.adapter = adapter
                    holder.rvOrders.visibility = View.VISIBLE
                } else {
                    holder.rvOrders.visibility = View.GONE
                    holder.expandIcon.setImageResource(R.drawable.ic_vector_arrow_circle_down)
                }
            }
        }
    }
    companion object {
        private const val TAG = "PaymentCustomerAdapter"
    }
    inner class PaymentCustomerViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.tv_item_payment_name)
        val phone: TextView = view.findViewById(R.id.tv_item_payment_phone)
        val rvOrders: RecyclerView = view.findViewById(R.id.rv_payment_customer_orders)
        val expandIcon: ImageView = view.findViewById(R.id.iv_item_payment_expand_icon)
    }
}

