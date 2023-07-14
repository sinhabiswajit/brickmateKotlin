package com.example.brickmate.ui.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityPaymentActionBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Order
import com.example.brickmate.model.Payment
import com.example.brickmate.model.Product
import com.example.brickmate.ui.adapters.PaymentActionAdapter
import com.example.brickmate.ui.fragments.OrderDetailDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import kotlin.collections.ArrayList

class PaymentActionActivity : BaseActivity(), View.OnClickListener {

    private lateinit var order: Order
    private lateinit var binding: ActivityPaymentActionBinding
    private val paymentList = mutableListOf<Payment>()
    private lateinit var adapter: PaymentActionAdapter
    private var isNewPaymentAdded: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        order = Order()
        order = intent.getParcelableExtra("order")!!
        isNewPaymentAdded = false

        if (order != null) {
            binding.tvPaymentActionCustomerName.text = order.customerName
            binding.tvPaymentActionPhone.text = order.phone
            binding.tvPaymentActionOrderId.text = order.orderId
            binding.tvPaymentActionOrderDate.text = order.orderDate
            binding.tvPaymentActionPaidStatus.text = if (order.paid) "Paid" else "Pending"
            if (binding.tvPaymentActionPaidStatus.text == "Pending") {
                binding.tvPaymentActionPaidStatus.setBackgroundResource(R.drawable.item_background_status_circular_red)
            } else {
                binding.tvPaymentActionPaidStatus.setBackgroundResource(R.drawable.item_background_status_circular_green)
            }
            binding.tvPaymentActionOrderTotal.text = "Rs ${"%.2f".format(order.total)}"
            binding.tvPaymentActionCheckOrder.setOnClickListener {
                showProductDialog()
            }
            Log.e("Payment List", order.paymentList.toString())
            adapter = PaymentActionAdapter(this, paymentList as ArrayList<Payment>, this)
            binding.rvBalanceLog.layoutManager = LinearLayoutManager(this)
            binding.rvBalanceLog.adapter = adapter

            updatePaymentRecView(order.paymentList)
            updatePaymentSection()

        }
        setUpActionBar()

        binding.tvAddPayment.setOnClickListener(this)
        binding.btnPaymentActionUpdateStatement.setOnClickListener(this)

    }

    private fun showProductDialog() {
        val dialog = OrderDetailDialogFragment.newInstance(order.productList as ArrayList<Product>)
        dialog.show(supportFragmentManager, "OrderDetailDialogFragment")
    }

    private fun setUpActionBar() {
        val toolbar = binding.toolbarPaymentActionActivity
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val ivIconBack = binding.icVectorBack
        ivIconBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.tv_add_payment -> showAddPaymentBottomSheet()
            R.id.btn_payment_action_update_statement ->{
                if (isNewPaymentAdded) {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FireStoreClass().updatePaymentListForCurrentOrder(this, paymentList, order.orderId)
                } else {
                    // Handle the case when the payment list is empty
                    Toast.makeText(this, "Add a new Payment first", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showAddPaymentBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView =
            LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_add_payment, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val etAmountPaid = bottomSheetView.findViewById<EditText>(R.id.et_bottom_sheet_payment_enter_amount)
        val etRemarks = bottomSheetView.findViewById<EditText>(R.id.et_bottom_sheet_payment_enter_remarks)
        val tvPaymentDate = bottomSheetView.findViewById<TextView>(R.id.tv_bottom_sheet_payment_date)

        // Get positive button view
        val positiveButton = bottomSheetView.findViewById<Button>(R.id.btn_bottom_sheet_payment_add)
        val cancelButton = bottomSheetView.findViewById<Button>(R.id.btn_bottom_sheet_payment_cancel)

        // Disable the "Add" button initially
        positiveButton.isEnabled = false

        // Add TextChangedListener to check if all fields are entered
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if all fields are entered
                val isAmountEntered = etAmountPaid.text.toString().isNotEmpty()
                val isRemarksEntered = etRemarks.text.toString().isNotEmpty()
                val isPaymentDateEntered = tvPaymentDate.text.toString().isNotEmpty()

                // Enable the "Add" button if all fields are entered
                positiveButton.isEnabled = isAmountEntered && isRemarksEntered && isPaymentDateEntered
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        }

        // Add the TextChangedListener to all fields
        etAmountPaid.addTextChangedListener(textWatcher)
        etRemarks.addTextChangedListener(textWatcher)
        tvPaymentDate.addTextChangedListener(textWatcher)

        tvPaymentDate.setOnClickListener {
            showDatePickerDialog(tvPaymentDate)
        }

        positiveButton.setOnClickListener {
            val amount = etAmountPaid.text.toString().toDouble()
            val remarks = etRemarks.text.toString()
            val date = tvPaymentDate.text.toString()

            val newPayment = Payment(date = date, remarks = remarks, amount = amount)
            paymentList.add(newPayment)

            isNewPaymentAdded = true

            // Refresh the RecyclerView to update the displayed payments
            //updatePaymentRecView(paymentList as ArrayList<Payment>)
            adapter.notifyDataSetChanged()
            updatePaymentSection()
            bottomSheetDialog.dismiss()
        }
        cancelButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun updatePaymentSection() {
        val totalAmountPaid = paymentList.sumByDouble { it.amount }
        binding.tvPaymentActionAmountPaid.text = "Rs ${"%.2f".format(totalAmountPaid)}"

        if (paymentList.size > 0) {
            binding.rvBalanceLog.visibility = View.VISIBLE
            binding.llNoPaymentRecordFound.visibility = View.GONE
        } else {
            binding.rvBalanceLog.visibility = View.GONE
            binding.llNoPaymentRecordFound.visibility = View.VISIBLE
        }
        val orderTotal = order.total
        val outstandingBalance = orderTotal - totalAmountPaid
        binding.tvPaymentActionOutstandingBal.text = "Rs ${"%.2f".format(outstandingBalance)}"

        // Show/hide the TextView based on the outstanding balance
        if (outstandingBalance <= 0) {
            binding.tvNoOutstandingBalanceMessage.visibility = View.VISIBLE
            binding.tvAddPayment.visibility = View.GONE
        } else {
            binding.tvNoOutstandingBalanceMessage.visibility= View.GONE
            binding.tvAddPayment.visibility = View.VISIBLE
        }
    }

    private fun updatePaymentRecView(paymentList: ArrayList<Payment>) {
        this.paymentList.clear()
        this.paymentList.addAll(paymentList)
        adapter.notifyDataSetChanged()

        if (this.paymentList.size > 0) {
            binding.rvBalanceLog.visibility = View.VISIBLE
            binding.llNoPaymentRecordFound.visibility = View.GONE
        } else {
            binding.rvBalanceLog.visibility = View.GONE
            binding.llNoPaymentRecordFound.visibility = View.VISIBLE
        }

        updatePaymentSection()
    }

    private fun showDatePickerDialog(tvPaymentDate: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            // Update the payment date TextView with the selected date
            val selectedDate = "${dayOfMonth}-${monthOfYear + 1}-${year}"
            tvPaymentDate.text = selectedDate
        }, year, month, day)

        // Show the date picker dialog
        datePickerDialog.show()
    }

    fun successUpdatePaymentList() {
        hideProgressDialog()
        Toast.makeText(this, "Payment Record Updated Successfully", Toast.LENGTH_SHORT).show()
    }

    fun deletePayment(position: Int) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Delete Payment")
            .setMessage("Are you sure you want to delete this payment?")
            .setPositiveButton("Delete") { dialog, _ ->
                paymentList.removeAt(position)
                adapter.notifyItemRemoved(position)
                updatePaymentSection()
                isNewPaymentAdded = true
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }
}