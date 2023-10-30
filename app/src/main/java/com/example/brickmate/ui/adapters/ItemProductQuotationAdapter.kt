package com.example.brickmate.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Product

class ItemProductQuotationAdapter(
    private val context: Context,
    private val productList : ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductQuotationViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_quotation_product,
                parent,
                false
            ))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product = productList[position]
        if (holder is ProductQuotationViewHolder){
            holder.productName.text = product.name
            holder.productPrice.text = product.sell_price.toString()
            holder.productQuantity.text = product.quantity.toString()
            holder.productGST.text = product.gst_rate.toString()
        }

    }

}
private class ProductQuotationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val productName: TextView = itemView.findViewById(R.id.tv_quotation_item_product_name)
    val productPrice: TextView = itemView.findViewById(R.id.tv_quotation_item_product_price)
    val productQuantity: TextView = itemView.findViewById(R.id.tv_quotation_item_product_quantity)
    val productGST: TextView = itemView.findViewById(R.id.tv_quotation_item_product_gst)
    val productCost: TextView = itemView.findViewById(R.id.tv_quotation_item_product_cost)
}

