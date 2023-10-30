package com.example.brickmate.ui.adapters

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.model.Quotation
import com.example.brickmate.ui.activities.ViewQuotationActivity
import com.example.brickmate.ui.fragments.QuotationListFragment

class QuotationAdapter(
    private val context: Context,
    private val quotationList: List<Quotation>,
    private val fragment: QuotationListFragment

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
        val model = quotationList[position]
        if (holder is EnquiryViewHolder) {
            holder.date.text = model.quotation_date
            holder.name.text = model.name
            holder.phone.text = "+91 ${model.phone}"
            holder.location.text = model.location
            holder.products.text = model.productList.joinToString(separator = ", ") { it.name }
            holder.additionalInfo.text = model.additional_info
            holder.ivDelete.setOnClickListener {
                fragment.deleteEnquiry(model.quotation_id)
            }
            holder.ivCallPhone.setOnClickListener {
                fragment.callEnquiry(model.phone)
            }
            holder.ivMessage.setOnClickListener {
                fragment.messageEnquiry(model.productList, model.phone, model.quotation_date)
            }
            // Create a SpannableString for "View Quotation" text
            val spannableString = SpannableString("View Quotation")

            // Apply underline to the text
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Set the SpannableString to the TextView
            holder.tvViewQuotation.text = spannableString
            holder.tvViewQuotation.setOnClickListener {
                //Toast.makeText(context, "View Quotation Clicked", Toast.LENGTH_LONG).show()
                val intent = Intent(context, ViewQuotationActivity::class.java)
                intent.putExtra("quotation_model", model)
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int {
        return quotationList.size
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
    val tvViewQuotation : TextView = view.findViewById(R.id.tv_enquiry_view_quotation)

}