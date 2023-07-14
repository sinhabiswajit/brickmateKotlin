package com.example.brickmate.ui.activities

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityOrderBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Address
import com.example.brickmate.model.Customer
import com.example.brickmate.model.Product
import com.example.brickmate.ui.adapters.AddressSpinnerAdapter
import com.example.brickmate.ui.adapters.ProductSpinnerAdapter
import com.example.brickmate.ui.adapters.SearchCustomerAdapter
import com.example.brickmate.util.Constants
import com.example.brickmate.util.CustomerClickInterface
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class OrderActivity : BaseActivity(), CustomerClickInterface, View.OnClickListener {
    private var binding: ActivityOrderBinding? = null
    private var isCustomerSelected = false
    private var customer: Customer = Customer()
    private lateinit var customerSearchList: ArrayList<Customer>
    private lateinit var tempCustomerSearchList: ArrayList<Customer>
    var selectedAddressString: String? = null
    var orderTotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initializeVars()
        setUpActionBar()
        getCustomerList()

        binding?.btnOrderAddProducts?.setOnClickListener(this)
        binding?.btnOrderCheckout?.setOnClickListener(this)
        binding?.btnOrderAddNewCustomer?.setOnClickListener(this)
        binding?.btnOrderActivityAddNewCustomer?.setOnClickListener(this)
    }

    private fun getCustomerList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getCustomerListFromFireStore(this@OrderActivity)
    }

    fun successGetCustomerListFromFireStore(searchCustomerList: ArrayList<Customer>) {
        hideProgressDialog()
        customerSearchList = searchCustomerList
        if (customerSearchList.size > 0){
            binding?.llOrderActivityNoCustomerAdded?.visibility = View.GONE
            binding?.rvOrderCustomerList?.visibility = View.VISIBLE
            updateRecyclerView(customerSearchList)
        }else{
            binding?.llOrderActivityNoCustomerAdded?.visibility = View.VISIBLE
            binding?.rvOrderCustomerList?.visibility = View.GONE
        }

    }

    private fun updateRecyclerView(customerList: ArrayList<Customer>) {
        if (customerList.size > 0){
            binding?.rvOrderCustomerList?.visibility = View.VISIBLE
            binding?.llOrderActivityNoCustomerAdded?.visibility = View.GONE
            binding?.rvOrderCustomerList?.layoutManager = LinearLayoutManager(this@OrderActivity)
            binding?.rvOrderCustomerList?.setHasFixedSize(true)
            val adapter = SearchCustomerAdapter(this@OrderActivity, customerList, this)
            binding?.rvOrderCustomerList?.adapter = adapter
        }else{
            binding?.rvOrderCustomerList?.visibility = View.GONE
            binding?.llOrderActivityNoCustomerAdded?.visibility = View.VISIBLE
        }

    }

    private fun initializeVars() {
        customerSearchList = ArrayList()
        tempCustomerSearchList = ArrayList()
    }

    private fun setUpActionBar() {
        val toolbar = binding?.toolbarOrderActivity
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding?.icVectorBack?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding?.searchViewOrder?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                queryListener(query)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                queryListener(query)
                return false
            }
        })
    }

    private fun queryListener(query: String?) {
        tempCustomerSearchList.clear()
        val searchText = query!!.lowercase(Locale.getDefault())
        if (searchText.isNotEmpty()) {
            binding?.rvOrderCustomerList?.visibility = View.VISIBLE
            binding?.llOrderBeginProcess?.visibility = View.GONE
            binding?.svOrderActivity?.visibility = View.GONE
            customerSearchList.forEach {
                if (it.name.lowercase(Locale.getDefault())
                        .contains(searchText) || it.phone.contains(searchText)
                ) {
                    binding?.llNoResultsFound?.visibility = View.GONE
                    tempCustomerSearchList.add(it)
                }
            }
            updateRecyclerView(tempCustomerSearchList)
            if (tempCustomerSearchList.isEmpty()) {
                binding?.rvOrderCustomerList?.visibility = View.GONE
                binding?.llNoResultsFound?.visibility = View.VISIBLE
                binding?.llOrderActivityNoCustomerAdded?.visibility = View.GONE
            } else {
                binding?.rvOrderCustomerList?.visibility = View.VISIBLE
                updateRecyclerView(tempCustomerSearchList)
                binding?.llNoResultsFound?.visibility = View.GONE
                binding?.llOrderActivityNoCustomerAdded?.visibility = View.GONE
            }
        } else {
            if (!isCustomerSelected) {
                updateRecyclerView(customerSearchList)
                //binding?.llOrderBeginProcess?.visibility = View.VISIBLE
                binding?.llNoResultsFound?.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onItemClick(customer: Customer) {
        this.customer = customer
        isCustomerSelected = true
        binding?.rvOrderCustomerList?.visibility = View.GONE
        binding?.llOrderBeginProcess?.visibility = View.GONE
        binding?.llNoResultsFound?.visibility = View.GONE

        //Clear the searchView Query
        binding?.searchViewOrder?.setQuery("", false)
        binding?.searchViewOrder?.clearFocus()

        //Hide the keyboard
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding?.searchViewOrder?.windowToken, 0)

        //Make the customer detail section visible and display customer name and phone
        binding?.svOrderActivity?.visibility = View.VISIBLE
        //binding?.llOrderCustomerDetails?.visibility = View.VISIBLE

        binding?.tvOrderCustomerName?.text = customer.name
        binding?.tvOrderCustomerPhone?.text = "+91 ${customer.phone}"

        //Call fireStore for selected customer address
        FireStoreClass().getCustomerAddresses(this, customer.name)

        //Toast.makeText(this, "Customer name is: ${this.customer.name} and id is ${this.customer.customer_id}", Toast.LENGTH_SHORT).show()
        Log.e("Customer_id", this.customer.customer_id)
    }

    fun successAddressForSelectedCustomer(customerAddressList: ArrayList<Address>) {
        Log.e("customerAddress", customerAddressList.toString())

        val adapter = AddressSpinnerAdapter(this, customerAddressList)
        val addressSpinner = findViewById<Spinner>(R.id.spinner_customer_address)
        addressSpinner.adapter = adapter

        addressSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedAddress = addressSpinner.adapter.getItem(position) as Address
                val addressString =
                    "${selectedAddress.address}, ${selectedAddress.landmark}, ${selectedAddress.zipCode}"
                selectedAddressString = addressString

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_order_add_products -> {
                binding?.llContainerLayout?.visibility = View.VISIBLE
                val products = ArrayList<Product>()
                val productCollection =
                    FirebaseFirestore.getInstance().collection(Constants.PRODUCTS)
                productCollection.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val product = document.toObject(Product::class.java)
                            product.product_id = document.id
                            products.add(product)
                        }
                        val productDetailsLayout =
                            layoutInflater.inflate(R.layout.item_add_products, null)
                        setupProductSpinner(productDetailsLayout, products)
                        setupQuantityEditText(productDetailsLayout)
                        setupGstEditText(productDetailsLayout)
                        setupGstCheckBox(productDetailsLayout)
                        addProductDetailsLayout(productDetailsLayout)

                        if (binding?.llContainerLayout?.childCount!! > 0) {
                            binding?.llOrderCheckout?.visibility = View.VISIBLE
                        } else {
                            binding?.llOrderCheckout?.visibility = View.GONE
                        }
                    }.addOnFailureListener { exception ->
                        Log.e(ContentValues.TAG, "Error getting products: $exception")
                    }
            }
            R.id.btn_order_checkout -> {
                val intent = Intent(this, OrderSummaryActivity::class.java)
                var allFieldsFilled = validateFields()

                if (allFieldsFilled) {
                    val selectedProducts = prepareSelectedProducts()
                    val customerName = binding?.tvOrderCustomerName?.text.toString()
                    val customerPhone = binding?.tvOrderCustomerPhone?.text.toString()
                    //val subTotal = binding?.tvSubTotal?.text.toString()
                    //val gstCharge = binding?.tvGstCharge?.text.toString()
                    val total = orderTotal

                    // create a bundle to hold the selected products and sent it via extras
                    val bundle = Bundle()
                    bundle.putParcelableArrayList("selectedProducts", selectedProducts)
                    intent.putExtras(bundle)
                    // Put the selected products, customer name, and phone as extra in the intent
                    intent.putExtra("user_id", customer.user_id)
                    intent.putExtra("customer_id", customer.customer_id)
                    intent.putExtra("customerName", customerName)
                    intent.putExtra("customerPhone", customerPhone)
                    //intent.putExtra("subTotal", subTotal)
                    //intent.putExtra("gstCharge", gstCharge)
                    intent.putExtra("total", total)
                    intent.putExtra("selectedAddressString", selectedAddressString)

                    // start the OrderSummaryActivity
                    startActivity(intent)
                }
            }
            R.id.btn_order_add_new_customer -> {
                val intent = Intent(this@OrderActivity, AddCustomerActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_order_activity_add_new_customer -> {
                val intent = Intent(this@OrderActivity, AddCustomerActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupGstCheckBox(productDetailsLayout: View) {
        val gstCheckBox = productDetailsLayout.findViewById<CheckBox>(R.id.cb_apply_gst)
        gstCheckBox.setOnCheckedChangeListener { _, _ ->
            updateTotal()
        }
    }

    private fun setupGstEditText(productDetailsLayout: View) {
        val gstEditText = productDetailsLayout.findViewById<EditText>(R.id.et_enter_cart_gst)
        gstEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTotal()
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }

    private fun prepareSelectedProducts(): ArrayList<out Parcelable> {
        val selectedProducts = ArrayList<Product>()

        for (i in 0 until binding?.llContainerLayout?.childCount!!) {
            val layout = binding?.llContainerLayout?.getChildAt(i)
            val productSpinner = layout?.findViewById<Spinner>(R.id.spinner_cart_product_title)
            val selectedProduct = productSpinner?.selectedItem as Product
            val quantityEditText = layout.findViewById<EditText>(R.id.et_enter_cart_quantity)
            val quantity = quantityEditText?.text.toString().toInt()
            val gstEditText = layout.findViewById<EditText>(R.id.et_enter_cart_gst)
            val gst = gstEditText?.text.toString().toInt()
            val includeGstCheckBox = layout.findViewById<CheckBox>(R.id.cb_apply_gst)
            val isGstIncluded = includeGstCheckBox.isChecked && gst > 0

            selectedProduct.quantity = quantity
            selectedProduct.gst_rate = if (isGstIncluded) gst.toString() else "0"
            selectedProduct.gstIncluded = isGstIncluded

            selectedProducts.add(selectedProduct)
        }
        return selectedProducts
    }

    private fun validateFields(): Boolean {
        var allFieldsFilled = true

        for (i in 0 until binding?.llContainerLayout?.childCount!!) {
            val layout = binding?.llContainerLayout?.getChildAt(i) as LinearLayout
            val quantityEditText = layout.findViewById<EditText>(R.id.et_enter_cart_quantity)
            val quantity = quantityEditText.text.toString().trim()
            val gstEditText = layout.findViewById<EditText>(R.id.et_enter_cart_gst)
            val gst = gstEditText.text.toString().trim()
            if (quantity.isEmpty()) {
                allFieldsFilled = false
                quantityEditText.error = "Please enter quantity"
            }
        }

        return allFieldsFilled
    }

    private fun setupProductSpinner(productDetailsLayout: View, products: ArrayList<Product>) {
        val adapter = ProductSpinnerAdapter(this, products)
        val productSpinner =
            productDetailsLayout.findViewById<Spinner>(R.id.spinner_cart_product_title)
        productSpinner.adapter = adapter

        productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedProduct = parent?.getItemAtPosition(position) as Product

                val sellPrice =
                    productDetailsLayout.findViewById<TextView>(R.id.tv_spinner_product_item_price)
                sellPrice.text = "Rs ${selectedProduct.sell_price}"

                val uom =
                    productDetailsLayout.findViewById<TextView>(R.id.tv_spinner_product_item_uom)
                uom.text = selectedProduct.uom
                val gstEditText =
                    productDetailsLayout.findViewById<EditText>(R.id.et_enter_cart_gst)
                gstEditText.setText(selectedProduct.gst_rate)
                updateTotal()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupQuantityEditText(productDetailsLayout: View) {
        val quantityEditText =
            productDetailsLayout.findViewById<EditText>(R.id.et_enter_cart_quantity)
        quantityEditText.setText("")
        quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTotal()
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }

    private fun addProductDetailsLayout(productDetailsLayout: View) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(
            resources.getDimensionPixelSize(R.dimen.layout_margin),
            resources.getDimensionPixelSize(R.dimen.layout_margin),
            resources.getDimensionPixelSize(R.dimen.layout_margin),
            0
        )
        binding?.llContainerLayout?.addView(productDetailsLayout, layoutParams)

        val ivRemoveProduct = productDetailsLayout.findViewById<ImageView>(R.id.iv_remove_product)
        ivRemoveProduct.setOnClickListener {
            binding?.llContainerLayout?.removeView(productDetailsLayout)
            updateTotal()
            if (binding?.llContainerLayout?.childCount!! <= 0) {
                binding?.llOrderCheckout?.visibility = View.GONE
            }
        }

    }

    private fun updateTotal() {

        orderTotal = 0.0

        // Iterate through all the inflated layouts and calculate the total price
        for (i in 0 until binding?.llContainerLayout?.childCount!!) {
            val inflatedLayout = binding?.llContainerLayout?.getChildAt(i)
            val quantityEditText =
                inflatedLayout?.findViewById<EditText>(R.id.et_enter_cart_quantity)
            val gstEditText = inflatedLayout?.findViewById<EditText>(R.id.et_enter_cart_gst)
            val selectedProduct =
                inflatedLayout?.findViewById<Spinner>(R.id.spinner_cart_product_title)?.selectedItem as? Product
            val applyGstCheckBox = inflatedLayout?.findViewById<CheckBox>(R.id.cb_apply_gst)
            val quantity = quantityEditText?.text.toString().toIntOrNull() ?: 0
            val price = selectedProduct?.sell_price?.toDouble()
            val gst = if (gstEditText?.text.toString().isEmpty()) {
                0.0
            } else {
                gstEditText?.text.toString().toDouble()
            }
            val applyGst = applyGstCheckBox?.isChecked

            //Calculate Product subTotal, gst charge and Product Total for every inflated layout
            val productSubTotal = price?.times(quantity)
            val productGstCharge = if (applyGst == true) {
                productSubTotal?.times(gst / 100.0)
            } else {
                0.0
            }
            val productTotal = productSubTotal?.plus(productGstCharge!!)

            val subtotalTextView = inflatedLayout?.findViewById<TextView>(R.id.tv_product_sub_total)
            subtotalTextView?.text = "Subtotal: Rs ${"%.2f".format(productSubTotal)}"

            val gstChargeTextView =
                inflatedLayout?.findViewById<TextView>(R.id.tv_product_gst_charge)
            gstChargeTextView?.text = "GST Charge: Rs ${"%.2f".format(productGstCharge)}"

            val productTotalTextView = inflatedLayout?.findViewById<TextView>(R.id.tv_product_total)
            productTotalTextView?.text = "Product Total: Rs ${"%.2f".format(productTotal)}"

            if (productTotal != null) {
                orderTotal += productTotal
            }

        }
        binding?.tvTotalAmount?.text = "Rs ${"%.2f".format(orderTotal)}"
    }
}