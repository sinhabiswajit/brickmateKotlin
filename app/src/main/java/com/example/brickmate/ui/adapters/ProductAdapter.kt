package com.example.brickmate.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Product
import com.example.brickmate.ui.activities.ProductDetailsActivity
import com.example.brickmate.util.Constants
import com.example.brickmate.util.GlideLoader

class ProductAdapter(
    private val context: Context,
    private var productList : ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_products,
                parent,
                false
            ))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = productList[position]
        if (holder is ProductViewHolder){
            holder.productName.text = model.name
            holder.productSellPrice.text = "Rs ${model.sell_price}"
            holder.productUOM.text = model.uom
            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                context.startActivity(intent)
            }

        }
    }
}
private class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val productName : TextView = view.findViewById(R.id.tv_product_item_title)
    val productUOM : TextView = view.findViewById(R.id.tv_product_item_uom)
    val productSellPrice : TextView = view.findViewById(R.id.tv_product_item_price)
}