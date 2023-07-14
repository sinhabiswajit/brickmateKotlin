package com.example.brickmate.ui.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Product
import com.example.brickmate.ui.activities.OrderDetailsUpdateActivity

class ProductOrderUpdateAdapter(
    private val context: Context,
    private var productList: ArrayList<Product>,
) : RecyclerView.Adapter<ProductOrderUpdateAdapter.ViewHolder>() {


    // Declare interface for delete button click listener
    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    private var onDeleteClickListener: OnDeleteClickListener? = null

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName: TextView = view.findViewById(R.id.tv_item_order_update_product_name)
        val tvQuantity: TextView = view.findViewById(R.id.tv_item_order_update_quantity)
        val tvSellPrice: TextView = view.findViewById(R.id.tv_item_order_update_product_sell_price)
        val tvUnit: TextView = view.findViewById(R.id.tv_item_order_update_product_uom)
        val tvProductTotal: TextView = view.findViewById(R.id.tv_item_order_update_product_total)
        val tvGstIncluded : TextView = view.findViewById(R.id.tv_item_order_update_gst_info)
        val ivDelete: ImageView = view.findViewById(R.id.iv_item_order_details_update_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_detail_update, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.tvProductName.text = product.name
        holder.tvSellPrice.text = "Rs ${product.sell_price}"
        holder.tvQuantity.text = "Qty : ${product.quantity}"
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
        holder.ivDelete.setOnClickListener {
            onDeleteClickListener?.onDeleteClick(position)
        }
    }

    override fun getItemCount() = productList.size
}