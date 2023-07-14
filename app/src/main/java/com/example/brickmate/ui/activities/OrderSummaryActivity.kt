package com.example.brickmate.ui.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityOrderSummaryBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Order
import com.example.brickmate.model.Product
import com.example.brickmate.ui.adapters.OrderSummaryAdapter

class OrderSummaryActivity : BaseActivity() {
    private var toolbarOrderSummaryActivity: Toolbar? = null

    private lateinit var binding: ActivityOrderSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeVars()
        setUpActionBar()
        val userId = intent.getStringExtra("user_id")
        val customerId = intent.getStringExtra("customer_id")
        val customerName = intent.getStringExtra("customerName")
        val customerPhone = intent.getStringExtra("customerPhone")
        val selectedAddressString = intent.getStringExtra("selectedAddressString")
        val productsList = intent.getSerializableExtra("selectedProducts") as ArrayList<Product>
        val total = intent.getDoubleExtra("total", 0.0)

        binding.tvOrderCustomerName.text = customerName
        binding.tvOrderCustomerPhone.text = customerPhone
        binding.tvOrderCustomerAddress.text = selectedAddressString

        val adapter = OrderSummaryAdapter(this, productsList)
        binding.rvOrderProductsList.layoutManager = LinearLayoutManager(this)
        binding.rvOrderProductsList.adapter = adapter

        //binding.tvOrderValueSubTotal.text = "₹ $subtotal"
        //binding.tvOrderValueGstCharge.text = "₹ $gstCharge"
        binding.tvOrderValueTotalAmount.text = "Rs ${"%.2f".format(total)}"

        binding.btnPlaceOrder.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            // Save the order to FireStore
            val order = Order(
                user_id = userId!!,
                orderId = generateOrderId(), // Generate a unique order ID here
                customerId = customerId!!,
                customerName = customerName!!,
                phone = customerPhone!!,
                address = selectedAddressString!!,
                productList = productsList,
                //subTotal = subtotal!!.toDouble(),
                //gstCharge = gstCharge!!.toDouble(),
                total = total,
                paid = false,
                orderDate = generateOrderDate(),
                delivered = false
            )
            Log.e("Orders", order.toString())

            FireStoreClass().saveOrderToFireStore(this, order)
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbarOrderSummaryActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_light)
        }
        toolbarOrderSummaryActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initializeVars() {
        toolbarOrderSummaryActivity = findViewById(R.id.toolbar_order_summary_activity)
    }

    fun successSaveOrderToFireStore() {
        hideProgressDialog()
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_order_success)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val tvSuccessMessage = dialog.findViewById<TextView>(R.id.tv_message_order_success)
        tvSuccessMessage.text = "Order placed successfully!"

        val btnOkay = dialog.findViewById<Button>(R.id.btn_ok)
        btnOkay.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        dialog.show()

    }
}


