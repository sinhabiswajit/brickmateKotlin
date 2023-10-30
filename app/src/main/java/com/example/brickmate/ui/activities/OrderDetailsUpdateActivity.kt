package com.example.brickmate.ui.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityOrderDetailsUpdateBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Order
import com.example.brickmate.model.Product
import com.example.brickmate.ui.adapters.ProductOrderUpdateAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderDetailsUpdateActivity : BaseActivity(),
    ProductOrderUpdateAdapter.OnDeleteClickListener, View.OnClickListener {
    private var order: Order? = null
    private var productsList: ArrayList<Product> = ArrayList()
    private var productItemList: ArrayList<Product> = ArrayList()
    private lateinit var binding: ActivityOrderDetailsUpdateBinding

    private var total: Double = 0.0
    private val calendar: Calendar = Calendar.getInstance()

    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDeliveryDate()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        getProductList()

        order = intent.getParcelableExtra<Order>("order_model")
        if (order != null) {

            binding.tvOrderListDetailUpdateOrderId.text = order!!.orderId
            binding.tvOrderListDetailOrderDate.text = order!!.orderDate
            binding.tvOrderListDetailUpdateCustomerName.text = order!!.customerName
            binding.tvOrderListDetailUpdateCustomerPhone.text = order!!.phone
            binding.tvOrderListDetailUpdateCustomerAddress.text = order!!.address
            binding.tvOrderListDetailUpdateTotal.text = order!!.total.toString()
            binding.tvDeliveryDateDatePicker.text = order!!.deliveryDate
            productItemList = order!!.productList as ArrayList<Product>
            for (product in productItemList) {
                Log.e("product : ", product.toString())
            }
            updateProductRecView()
            calculateTotalAndUpdateUI()

            val deliveryStatusSpinner = binding.spinnerDeliveryStatus
            ArrayAdapter.createFromResource(
                this,
                R.array.delivery_status_options,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                deliveryStatusSpinner.adapter = adapter
            }

            val paidStatusSpinner = binding.spinnerPaymentStatus
            ArrayAdapter.createFromResource(
                this,
                R.array.paid_status_options,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                paidStatusSpinner.adapter = adapter
            }

            // Get default delivery and paid status values from intent
            val defaultDeliveryStatus = order!!.delivered
            val defaultPaidStatus = order!!.paid

            // Set default selection for delivery status spinner
            val deliveryStatusPosition = if (defaultDeliveryStatus) 0 else 1
            deliveryStatusSpinner.setSelection(deliveryStatusPosition)

            // Set default selection for paid status spinner
            val paidStatusPosition = if (defaultPaidStatus) 0 else 1
            paidStatusSpinner.setSelection(paidStatusPosition)

            if (deliveryStatusPosition == 1) {
                binding.tvDeliveryDateDatePicker.text = ""
            }

            // Add listener to delivery status spinner
            deliveryStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if (position == 1) {
                        binding.tvDeliveryDateDatePicker.text = ""
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

            binding.btnUpdateProductList.setOnClickListener {
                showAddProductBottomSheet()
            }

            // Set click listener for update button
            binding.btnOrderDetailsUpdateUpdate.setOnClickListener {
                val deliveryStatus = deliveryStatusSpinner.selectedItem.toString() == "Delivered"
                val paidStatus = paidStatusSpinner.selectedItem.toString() == "Paid"
                if (productItemList.size > 0) {
                    if (deliveryStatus && binding.tvDeliveryDateDatePicker.text.isEmpty()) {
                        Toast.makeText(this, "Please select a delivery date", Toast.LENGTH_SHORT)
                            .show()
                    } else if (!deliveryStatus && binding.tvDeliveryDateDatePicker.text.isNotEmpty()) {
                        Toast.makeText(this, "Set Delivery status to delivered", Toast.LENGTH_SHORT).show()
                    } else {
                        val updatedOrder = mapOf(
                            "productList" to productItemList,
                            "paid" to paidStatus,
                            "delivered" to deliveryStatus,
                            "deliveryDate" to binding.tvDeliveryDateDatePicker.text.toString(),
                            "total" to total
                        )
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FireStoreClass().updateOrder(this, updatedOrder, order!!.orderId)
                    }
                } else {
                    Toast.makeText(this, "Add products to order!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.tvDeliveryDateDatePicker.setOnClickListener(this)
    }

    private fun updateProductRecView() {
        val adapter = ProductOrderUpdateAdapter(this, productItemList)
        adapter.setOnDeleteClickListener(this)
        binding.rvOrderListDetailUpdateProducts.layoutManager = LinearLayoutManager(this)
        binding.rvOrderListDetailUpdateProducts.adapter = adapter
    }

    private fun calculateTotalAndUpdateUI() {
        total = 0.0
        for (productItem in productItemList) {
            val productTotal: Double = productItem.sell_price.toDouble() * productItem.quantity

            // Check if GST was included
            total += if (productItem.gstIncluded) {
                // Add GST value to the product total
                val gstAmount: Double = productTotal * (productItem.gst_rate.toDouble() / 100.0)
                productTotal + gstAmount
            } else {
                productTotal
            }
        }
        binding.tvOrderListDetailUpdateTotal.text = "Rs ${"%.2f".format(total)}"
    }

    private fun getProductList() {
        FireStoreClass().getProductListFromFireStore(this@OrderDetailsUpdateActivity)
    }

    private fun showAddProductBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView =
            LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_add_product, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        // Initialize views and set up listeners
        // val productTotal = bottomSheetView.findViewById<TextView>(R.id.tv_bottom_sheet_product_total)
        val spinner = bottomSheetView.findViewById<Spinner>(R.id.spinner_bottom_sheet_product_title)
        val etQuantity =
            bottomSheetView.findViewById<EditText>(R.id.et_bottom_sheet_product_quantity)
        val etGST = bottomSheetView.findViewById<EditText>(R.id.et_bottom_sheet_product_gst)
        val tvPrice = bottomSheetView.findViewById<TextView>(R.id.tv_dialog_product_price)
        val tvUnit = bottomSheetView.findViewById<TextView>(R.id.tv_dialog_product_uom)
        val checkboxGst = bottomSheetView.findViewById<CheckBox>(R.id.cb_bottom_sheet_apply_gst)

        // set up the spinner with the list of products
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            productsList.map { it.name }
        )
        spinner.adapter = adapter

        // set up the spinner onItemSelected listener to update the price and unit
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedProduct = productsList[position]
                tvPrice.text = "Rs ${selectedProduct.sell_price}"
                tvUnit.text = selectedProduct.uom
                etGST.setText(selectedProduct.gst_rate)
                // Update the product total when the spinner selection changes
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        // Get positive button view
        val positiveButton = bottomSheetView.findViewById<Button>(R.id.btn_bottom_sheet_add)
        val cancelButton = bottomSheetView.findViewById<Button>(R.id.btn_bottom_sheet_cancel)

        // Disable the positive button initially
        positiveButton.isEnabled = false

        // Add a TextChangedListener to the quantity EditText
        etQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val quantity = s.toString().toIntOrNull()
                val gst = etGST.text.toString().toIntOrNull()

                // Enable the positive button if both quantity and gst are greater than 0
                positiveButton.isEnabled =
                    (quantity != null && quantity > 0 && gst != null && gst > 0)

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        // Add a TextChangedListener to the quantity EditText
        etGST.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val quantity = etQuantity.text.toString().toIntOrNull()
                val gst = s.toString().toIntOrNull()

                // Enable the positive button if both quantity and gst are greater than 0
                positiveButton.isEnabled =
                    (quantity != null && quantity > 0 && gst != null && gst > 0)
                // Update the product total when the checkbox state changes
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        // Add a listener to the "Apply GST" checkbox
        checkboxGst.setOnCheckedChangeListener { _, _ ->
            val quantity = etQuantity.text.toString().toIntOrNull()
            val gst = etGST.text.toString().toIntOrNull()

        }
        // Set the positive button's OnClickListener to add the product to the list
        positiveButton.setOnClickListener {
            val selectedPosition = spinner.selectedItemPosition
            val selectedProduct = getCurrentSelectedProduct(selectedPosition)
            val quantity = etQuantity.text.toString().toInt()
            val gstIncluded = checkboxGst.isChecked
            val gst = if (gstIncluded) {
                etGST.text.toString()
            } else {
                ""
            }

            val defaultProduct = Product(FireStoreClass().getCurrentUserID(),"", "", "", "", "", "", "", 0, false, false)

            val newProductItem = Product(
                FireStoreClass().getCurrentUserID(),
                selectedProduct.product_id,
                selectedProduct.name,
                selectedProduct.description,
                selectedProduct.uom,
                selectedProduct.sell_price,
                gst,
                selectedProduct.product_image,
                quantity,
                isSelected = false,
                gstIncluded = gstIncluded
            )

            productItemList.add(newProductItem)
            updateProductRecView()
            calculateTotalAndUpdateUI()

            bottomSheetDialog.dismiss()
        }
        cancelButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun checkEnableAddButton(
        etQuantity: EditText,
        etGST: EditText,
        checkboxGst: CheckBox,
        positiveButton: Button
    ) {
        val quantity = etQuantity.text.toString().toIntOrNull()
        val gst = etGST.text.toString().toIntOrNull()
        positiveButton.isEnabled =
            (quantity != null && quantity > 0 && (!checkboxGst.isChecked || (gst != null && gst > 0)))
    }

    private fun getCurrentSelectedProduct(position: Int): Product {
        return productsList.getOrNull(position) ?: Product(
            FireStoreClass().getCurrentUserID(),
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            0,
            false,
            false
        )
    }


    private fun setUpActionBar() {
        val toolbar = binding.toolbarOrderListDetailUpdateActivity
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_light)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun successGetProductListFromFireStore(productsList: java.util.ArrayList<Product>) {
        this.productsList = productsList
    }


    override fun onDeleteClick(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Remove Product")
        builder.setMessage("Are you sure you want to remove this product from Order?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            productItemList.removeAt(position)
            updateProductRecView()
            calculateTotalAndUpdateUI()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    fun successOrderUpdate() {
        hideProgressDialog()
        //Give toast message to user here that order has been successfully updated
        Toast.makeText(this, "Order updated successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@OrderDetailsUpdateActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_delivery_date_date_picker -> {
                showDatePicker()
            }
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateDeliveryDate() {
        val dateFormat = "dd-MM-yyyy"
        val formattedDate = SimpleDateFormat(dateFormat, Locale.getDefault()).format(calendar.time)
        binding.tvDeliveryDateDatePicker.text = formattedDate
    }


}