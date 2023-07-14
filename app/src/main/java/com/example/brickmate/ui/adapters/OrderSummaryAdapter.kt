package com.example.brickmate.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Product

class OrderSummaryAdapter(
    private val context: Context,
    private val productList: ArrayList<Product>
) : RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName: TextView = view.findViewById(R.id.tv_item_order_product_name)
        val tvQuantity: TextView = view.findViewById(R.id.tv_item_order_quantity)
        val tvSellPrice: TextView = view.findViewById(R.id.tv_item_order_product_sell_price)
        val tvUnit : TextView = view.findViewById(R.id.tv_item_order_product_uom)
        val tvProductTotal : TextView = view.findViewById(R.id.tv_item_order_product_total)
        val tvGstIncluded : TextView = view.findViewById(R.id.tv_gst_included_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place_order_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        Log.e("Product", product.toString())
        holder.tvProductName.text = product.name
        holder.tvQuantity.text = "${product.quantity} ${product.uom}"
        holder.tvSellPrice.text = "Rs ${product.sell_price}"
        holder.tvUnit.text = product.uom
        val gst = product.gst_rate
        val includeGst = product.gstIncluded
        val productTotal = if (includeGst) {
            product.sell_price.toDouble() * product.quantity * (1 + gst.toDouble() / 100.0)
        } else {
            product.sell_price.toDouble() * product.quantity
        }
        holder.tvProductTotal.text = "Rs ${"%.2f".format(productTotal)}"
        if (includeGst){
            holder.tvGstIncluded.visibility = View.VISIBLE
            holder.tvGstIncluded.text = "(${gst}% gst included)"
        }else{
            holder.tvGstIncluded.visibility = View.GONE
        }
    }

    override fun getItemCount() = productList.size
}