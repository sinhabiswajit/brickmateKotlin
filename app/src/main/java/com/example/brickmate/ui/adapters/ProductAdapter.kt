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
            GlideLoader(context).loadProductPicture(model.product_image, holder.productImage)
            holder.productName.text = model.name
            holder.productDescription.text = model.description
            holder.productUOM.text = model.uom
            holder.productSellPrice.text = model.sell_price
            holder.productGSTRate.text = model.gst_rate

        }
    }
}
private class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val productImage : ImageView = view.findViewById(R.id.iv_product_image)
    val productName : TextView = view.findViewById(R.id.tv_product_name)
    val productDescription : TextView = view.findViewById(R.id.tv_product_description)
    val productUOM : TextView = view.findViewById(R.id.tv_product_uom)
    val productSellPrice : TextView = view.findViewById(R.id.tv_product_sell_price)
    val productGSTRate : TextView = view.findViewById(R.id.tv_product_gst_rate)

}