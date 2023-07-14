package com.example.brickmate.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityOrderListDetailBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Order
import com.example.brickmate.model.Product
import com.example.brickmate.ui.adapters.OrderSummaryAdapter

class OrderListDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityOrderListDetailBinding
    private var toolBarOrderListDetailActivity : Toolbar? = null
    private lateinit var rvOrderListDetailProducts : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeVars()
        setUpActionBar()

        val order = intent.getParcelableExtra<Order>("order")

        if (order != null) {
            // use the order object here
            binding.tvOrderListDetailOrderId.text = order.orderId
            binding.tvOrderListDetailOrderDate.text = order.orderDate
            binding.tvOrderListDetailCustomerName.text = order.customerName
            binding.tvOrderListDetailCustomerPhone.text = order.phone
            binding.tvOrderListDetailCustomerAddress.text = order.address
            binding.tvOrderListDetailTotal.text = "Rs ${"%.2f".format(order.total)}"
            if (!order.deliveryDate.isEmpty()){
                binding.llDeliveryDate.visibility = View.VISIBLE
                binding.tvOrderListDetailDeliveryDate.text = order.deliveryDate
            }else{
                binding.llDeliveryDate.visibility = View.GONE
            }
            if (order.paid){
                binding.tvOrderListDetailIsPaid.text = "Paid"
                binding.tvOrderListDetailIsPaid.setTextColor(ContextCompat.getColor(this, R.color.green))
            }else{
                binding.tvOrderListDetailIsPaid.text = "Pending"
                binding.tvOrderListDetailIsPaid.setTextColor(ContextCompat.getColor(this, R.color.red))
            }
            if (order.delivered){
                binding.tvOrderListDetailIsDelivered.text = "Delivered"
                binding.tvOrderListDetailIsDelivered.setTextColor(ContextCompat.getColor(this, R.color.green))
            }else{
                binding.tvOrderListDetailIsDelivered.text = "Pending"
                binding.tvOrderListDetailIsDelivered.setTextColor(ContextCompat.getColor(this, R.color.red))
            }
            val adapter = OrderSummaryAdapter(this, order.productList as ArrayList<Product>)
            binding.rvOrderListDetailProducts.layoutManager = LinearLayoutManager(this)
            binding.rvOrderListDetailProducts.adapter = adapter
        } else {
            // handle the null case here
        }
        Log.e("ORDER_MODEL", order.toString())
        Log.e("ORDER_ID", order?.orderId.toString())
        binding.btnOrderDetailsDelete.setOnClickListener {
            if (order != null) {
                deleteOrder(order.orderId)
            }
        }
        binding.btnOrderDetailsUpdate.setOnClickListener {
            if (order!=null){
                val intent = Intent(this@OrderListDetailActivity, OrderDetailsUpdateActivity::class.java)
                intent.putExtra("order_model", order)
                startActivity(intent)
            }
        }
    }

    private fun deleteOrder(orderId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Order")
        builder.setMessage("Are you sure you want to delete this Order?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().deleteOrderFromFireStore(this, orderId)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolBarOrderListDetailActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_light)
        }
        toolBarOrderListDetailActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initializeVars() {
        toolBarOrderListDetailActivity = findViewById(R.id.toolbar_order_list_detail_activity)
        rvOrderListDetailProducts = findViewById(R.id.rv_order_list_detail_products)
    }

    fun successDeleteOrder() {
        hideProgressDialog()
        Toast.makeText(this, "Order Deleted Successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
}