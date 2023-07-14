package com.example.brickmate.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Product

class ProductEnquiryAdapter(
    private val context: Context,
    private var productList: ArrayList<Product>
) : RecyclerView.Adapter<ProductEnquiryAdapter.ViewHolder>() {

    // Declare interface for delete button click listener
    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    private var onDeleteClickListener: OnDeleteClickListener? = null

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName: TextView = view.findViewById(R.id.tv_item_enquiry_product_name)
        val tvQuantity: TextView = view.findViewById(R.id.tv_item_enquiry_product_quantity)
        val tvSellPrice: TextView = view.findViewById(R.id.tv_item_enquiry_product_price)
        val tvUnit: TextView = view.findViewById(R.id.tv_item_enquiry_product_unit)
        val tvProductTotal: TextView = view.findViewById(R.id.tv_item_enquiry_product_total)
        val ivDelete: ImageView = view.findViewById(R.id.iv_item_enquiry_product_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_enquiry, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.tvProductName.text = product.name
        holder.tvQuantity.text = product.quantity.toString()
        holder.tvSellPrice.text = "Rs ${product.sell_price}"
        holder.tvUnit.text = product.uom
        val total = product.sell_price.toDouble() * product.quantity
        holder.tvProductTotal.text = "Rs $total"
        holder.ivDelete.setOnClickListener {
            onDeleteClickListener?.onDeleteClick(position)
        }
    }
}