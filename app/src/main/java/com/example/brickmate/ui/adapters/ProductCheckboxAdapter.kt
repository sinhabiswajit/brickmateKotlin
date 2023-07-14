package com.example.brickmate.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Product

class ProductCheckboxAdapter(
    private val context: Context,
    private val productList: List<Product>,
    private val onProductClickListener: OnProductClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val selectedProducts: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductCheckboxViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_product_checkbox,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product = productList[position]
        if (holder is ProductCheckboxViewHolder) {
            holder.productNameTextView.text = product.name
            holder.productCheckBox.isChecked = selectedProducts.contains(product)
            holder.productCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedProducts.add(product)
                } else {
                    selectedProducts.remove(product)
                }
            }
            holder.itemView.setOnClickListener {
                onProductClickListener.onProductClick(product)
            }
        }
    }

    fun getSelectedProducts(): List<Product> {
        return selectedProducts
    }

    fun clearSelections() {
        selectedProducts.clear()
        productList.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    interface OnProductClickListener {
        fun onProductClick(product: Product)
    }

}

private class ProductCheckboxViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val productNameTextView: TextView = view.findViewById(R.id.checkbox_product_name)
    val productCheckBox: CheckBox = view.findViewById(R.id.checkbox_product)

}
