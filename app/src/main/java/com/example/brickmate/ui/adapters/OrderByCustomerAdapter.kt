package com.example.brickmate.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Order
import com.example.brickmate.model.Product
import com.example.brickmate.ui.activities.OrderListDetailActivity
import com.example.brickmate.ui.activities.PaymentActionActivity

class OrderByCustomerAdapter(
    private val context : Context,
    private var orders : ArrayList<Order>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrderByCustomerViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_order_list,
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
        if (holder is OrderByCustomerViewHolder) {
            holder.orderId.text = model.orderId
            holder.orderDate.text = model.orderDate
            holder.orderTotal.text = "Rs ${model.total}"
            if (!model.delivered){
                holder.deliveryStatus.setBackgroundResource(R.drawable.item_background_status_circular_red)
                holder.deliveryStatus.text = "Not Delivered"
            }else{
                holder.deliveryStatus.setBackgroundResource(R.drawable.item_background_status_circular_green)
                holder.deliveryStatus.text = "Delivered"
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(context, OrderListDetailActivity::class.java)
                intent.putExtra("order", model)
                context.startActivity(intent)
            }
        }
    }
}
private class OrderByCustomerViewHolder(view : View) : RecyclerView.ViewHolder(view){
    val orderId : TextView = view.findViewById(R.id.tv_item_order_id)
    val orderDate : TextView = view.findViewById(R.id.tv_item_order_date)
    val orderTotal : TextView = view.findViewById(R.id.tv_item_order_total)
    val deliveryStatus : TextView = view.findViewById(R.id.tv_item_order_delivery_status)
}