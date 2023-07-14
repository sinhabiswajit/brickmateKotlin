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
import com.example.brickmate.model.Payment
import com.example.brickmate.ui.activities.PaymentActionActivity

class PaymentActionAdapter(
    private val context : Context,
    private var paymentList : ArrayList<Payment>,
    private val activity: PaymentActionActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PaymentActionViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_payment_action,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = paymentList[position]
        if (holder is PaymentActionViewHolder) {
            holder.date.text = model.date
            holder.remarks.text = model.remarks
            holder.amountPaid.text = "₹${model.amount} +"
            holder.itemView.isActivated = position % 2 != 0
            holder.ivDelete.setOnClickListener {
                activity.deletePayment(position)
            }
        }
    }
}
private class PaymentActionViewHolder(view : View) : RecyclerView.ViewHolder(view){
    val date : TextView = view.findViewById(R.id.tv_item_payment_action_date)
    val remarks : TextView = view.findViewById(R.id.tv_item_payment_action_remark)
    val amountPaid : TextView = view.findViewById(R.id.tv_item_payment_action_amount)
    val llPaymentAction : LinearLayout = view.findViewById(R.id.ll_item_payment_action)
    val ivDelete : ImageView = view.findViewById(R.id.iv_payment_action_delete_payment)
}