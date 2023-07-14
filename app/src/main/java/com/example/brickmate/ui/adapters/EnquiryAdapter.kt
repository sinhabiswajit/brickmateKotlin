package com.example.brickmate.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Enquiry
import com.example.brickmate.ui.fragments.EnquiryListFragment

class EnquiryAdapter(
    private val context: Context,
    private val enquiryList: List<Enquiry>,
    private val fragment: EnquiryListFragment

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EnquiryViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_enquiry,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = enquiryList[position]
        if (holder is EnquiryViewHolder) {
            holder.date.text = model.enquiry_date
            holder.name.text = model.name
            holder.phone.text = "+91 ${model.phone}"
            holder.location.text = model.location
            holder.products.text = model.productList.joinToString(separator = ", ") { it.name }
            holder.additionalInfo.text = model.additional_info
            holder.ivDelete.setOnClickListener {
                fragment.deleteEnquiry(model.enquiry_id)
            }
            holder.ivCallPhone.setOnClickListener {
                fragment.callEnquiry(model.phone)
            }
            holder.ivMessage.setOnClickListener {
                fragment.messageEnquiry(model.productList, model.phone, model.enquiry_date)
            }

        }
    }

    override fun getItemCount(): Int {
        return enquiryList.size
    }
}

private class EnquiryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val date: TextView = view.findViewById(R.id.tv_enquiry_date)
    val name: TextView = view.findViewById(R.id.tv_enquiry_name)
    val phone: TextView = view.findViewById(R.id.tv_enquiry_phone)
    val location: TextView = view.findViewById(R.id.tv_enquiry_location)
    val products: TextView = view.findViewById(R.id.tv_enquiry_product_list)
    val additionalInfo: TextView = view.findViewById(R.id.tv_enquiry_additional_info)
    val ivDelete: ImageView = view.findViewById(R.id.iv_enquiry_delete)
    val ivCallPhone: ImageView = view.findViewById(R.id.iv_enquiry_phone)
    val ivMessage: ImageView = view.findViewById(R.id.iv_enquiry_message)
}