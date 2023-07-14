package com.example.brickmate.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Order
import com.example.brickmate.ui.activities.PaymentActionActivity

class PaymentOrderAdapter(
    private val context : Context,
    private var orders : ArrayList<Order>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PaymentOrderViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_payment_order,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = orders[position]
        if (holder is PaymentOrderViewHolder) {
            holder.orderId.text = model.orderId
            holder.orderDate.text = model.orderDate
            holder.orderTotal.text = "Rs ${model.total}"
            if (!model.paid){
                holder.paymentStatus.setBackgroundResource(R.drawable.item_background_status_circular_red)
                holder.paymentStatus.text = "Pending"
            }else{
                holder.paymentStatus.setBackgroundResource(R.drawable.item_background_status_circular_green)
                holder.paymentStatus.text = "Paid"
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(context, PaymentActionActivity::class.java)
                intent.putExtra("order", model)
                context.startActivity(intent)
            }
    }
    }
}
private class PaymentOrderViewHolder(view : View) : RecyclerView.ViewHolder(view){
    val orderId : TextView = view.findViewById(R.id.tv_item_payment_order_id)
    val orderDate : TextView = view.findViewById(R.id.tv_item_payment_order_date)
    val orderTotal : TextView = view.findViewById(R.id.tv_item_payment_order_total)
    val paymentStatus : TextView = view.findViewById(R.id.tv_item_payment_order_paid_status)
}