package com.example.brickmate.ui.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
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
import kotlin.collections.ArrayList

class SelectCustomerActivity : BaseActivity(), CustomerClickInterface, View.OnClickListener {
    private var toolBarSelectCustomerActivity: Toolbar? = null
    private lateinit var rvSearchCustomer: RecyclerView
    private lateinit var customerSearchList: ArrayList<Customer>
    private lateinit var tempCustomerSearchList: ArrayList<Customer>
    private lateinit var llCartCustomer: LinearLayout
    private lateinit var llContainerLayout: LinearLayout
    private lateinit var llCheckout: LinearLayout
    private lateinit var svContainerLayout: ScrollView
    private var btnCartAddProducts: Button? = null
    private var tvCartCustomerName: TextView? = null
    private var tvCartCustomerPhone: TextView? = null
    private var tvSubTotal: TextView? = null
    private var tvTotal: TextView? = null
    private var tvGstCharge: TextView? = null
    private var searchView: SearchView? = null
    private var btnCheckOut: Button? = null
    private var spinnerCustomerAddress: Spinner? = null
    var selectedAddressString: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_customer)

        initializeVars()
        setUpActionBar()
        getCustomerList()

        btnCartAddProducts?.setOnClickListener(this)
        btnCheckOut?.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.order_list_menu, menu)
        val searchItem = menu.findItem(R.id.action_search_order_customer)
        searchItem.expandActionView()
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search for customer"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                queryListener(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                queryListener(newText)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search_order_customer -> {
                searchView = item.actionView as SearchView
                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        queryListener(query)
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        queryListener(newText)
                        return false
                    }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun queryListener(newText: String?) {
        tempCustomerSearchList.clear()
        val searchText = newText!!.lowercase(Locale.getDefault())
        if (searchText.isNotEmpty()) {
            customerSearchList.forEach {
                if (it.name.lowercase(Locale.getDefault())
                        .contains(searchText) || it.phone.contains(searchText)
                ) {
                    tempCustomerSearchList.add(it)
                }
            }
            rvSearchCustomer.layoutManager = LinearLayoutManager(this@SelectCustomerActivity)
            rvSearchCustomer.setHasFixedSize(true)
            val adapter =
                SearchCustomerAdapter(this@SelectCustomerActivity, tempCustomerSearchList, this)
            rvSearchCustomer.adapter = adapter
        }
    }

    private fun initializeVars() {
        toolBarSelectCustomerActivity = findViewById(R.id.toolbar_select_customer_activity)
        rvSearchCustomer = findViewById(R.id.rv_search_customer)
        tempCustomerSearchList = ArrayList()
        customerSearchList = ArrayList()
        llCartCustomer = findViewById(R.id.ll_cart_customer_detail)
        llContainerLayout = findViewById(R.id.ll_container_layout)
        svContainerLayout = findViewById(R.id.sv_container_layout)
        btnCartAddProducts = findViewById(R.id.btn_cart_add_product)
        tvCartCustomerName = findViewById(R.id.tv_cart_customer_name)
        tvCartCustomerPhone = findViewById(R.id.tv_cart_customer_phone)
        tvSubTotal = findViewById(R.id.tv_sub_total)
        tvGstCharge = findViewById(R.id.tv_gst_charge)
        tvTotal = findViewById(R.id.tv_total_amount)
        llCheckout = findViewById(R.id.ll_checkout)
        btnCheckOut = findViewById(R.id.btn_checkout)
        spinnerCustomerAddress = findViewById(R.id.spinner_customer_address)

    }

    private fun getCustomerList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getCustomerListFromFireStore(this@SelectCustomerActivity)
    }

    fun successGetCustomerListFromFireStore(searchCustomerList: ArrayList<Customer>) {
        hideProgressDialog()
        customerSearchList = searchCustomerList
    }

    override fun onItemClick(customer: Customer) {
        rvSearchCustomer.visibility = View.GONE
        llCartCustomer.visibility = View.VISIBLE
        btnCartAddProducts?.visibility = View.VISIBLE
        tvCartCustomerName?.text = customer.name
        tvCartCustomerPhone?.text = customer.phone
        //Toast.makeText(this, "Customer name is: ${customer.name}", Toast.LENGTH_SHORT).show()
        searchView?.setQuery("", false)
        searchView?.onActionViewCollapsed()
        FireStoreClass().getCustomerAddresses(this, customer.name)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_cart_add_product -> {
                svContainerLayout.visibility = View.VISIBLE
                llCheckout.visibility = View.VISIBLE
                val products = mutableListOf<Product>()
                val productCollection =
                    FirebaseFirestore.getInstance().collection(Constants.PRODUCTS)
                productCollection.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val product = document.toObject(Product::class.java)
                            products.add(product)
                        }
                        val productDetailsLayout =
                            layoutInflater.inflate(R.layout.item_add_products, null)
                        val adapter = ProductSpinnerAdapter(this, products)
                        val productSpinner =
                            productDetailsLayout.findViewById<Spinner>(R.id.spinner_cart_product_title)
                        productSpinner.adapter = adapter

                        productSpinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedProduct =
                                        parent?.getItemAtPosition(position) as Product

                                    val sellPrice =
                                        productDetailsLayout.findViewById<TextView>(R.id.tv_spinner_product_item_price)
                                    sellPrice.text = "Rs ${selectedProduct.sell_price}"

                                    val uom =
                                        productDetailsLayout.findViewById<TextView>(R.id.tv_spinner_product_item_uom)
                                    uom.text = selectedProduct.uom

                                    val quantityEditText =
                                        productDetailsLayout.findViewById<EditText>(R.id.et_enter_cart_quantity)
                                    quantityEditText.setText("")
                                    quantityEditText.addTextChangedListener(object : TextWatcher {
                                        override fun beforeTextChanged(
                                            s: CharSequence?,
                                            start: Int,
                                            count: Int,
                                            after: Int
                                        ) {
                                            // Do nothing
                                        }

                                        override fun onTextChanged(
                                            s: CharSequence?,
                                            start: Int,
                                            before: Int,
                                            count: Int
                                        ) {
                                            updateTotal()
                                        }

                                        override fun afterTextChanged(s: Editable?) {
                                            // Do nothing
                                        }
                                    })

                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    // Do nothing
                                }
                            }
                        // Add the inflated layout to the parent layout
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
                        llContainerLayout.addView(productDetailsLayout, layoutParams)

                        // Set the OnClickListener for the inflated layout to remove itself from the parent layout
                        val ivRemoveProduct =
                            productDetailsLayout.findViewById<ImageView>(R.id.iv_remove_product)
                        ivRemoveProduct.setOnClickListener {
                            llContainerLayout.removeView(productDetailsLayout)
                            updateTotal()
                        }

                    }.addOnFailureListener { exception ->
                        Log.e(TAG, "Error getting products: $exception")
                    }
            }
            R.id.btn_checkout -> {
                val intent = Intent(this, OrderSummaryActivity::class.java)
                var allFieldsFilled = true

                for (i in 0 until llContainerLayout.childCount) {
                    val layout = llContainerLayout.getChildAt(i) as LinearLayout
                    val quantityEditText =
                        layout.findViewById<EditText>(R.id.et_enter_cart_quantity)
                    val quantity = quantityEditText.text.toString().trim()
                    if (quantity.isEmpty()) {
                        allFieldsFilled = false
                        quantityEditText.error = "Please enter quantity"
                    }
                }
                if (allFieldsFilled) {
                    val selectedProducts = ArrayList<Product>()
                    for (i in 0 until llContainerLayout.childCount) {
                        val layout = llContainerLayout.getChildAt(i)
                        val productSpinner =
                            layout.findViewById<Spinner>(R.id.spinner_cart_product_title)
                        val selectedProduct = productSpinner.selectedItem as Product
                        val quantityEditText =
                            layout.findViewById<EditText>(R.id.et_enter_cart_quantity)
                        val quantity = quantityEditText.text.toString().toInt()
                        selectedProduct.quantity = quantity
                        selectedProducts.add(selectedProduct)
                    }
                    Log.e("SelectedProducts", "Selected Products $selectedProducts")

                    //Get the customer name, phone, selected address, subtotal, gstCharge, total
                    val customerName = tvCartCustomerName?.text.toString()
                    val customerPhone = tvCartCustomerPhone?.text.toString()
                    val subTotal = tvSubTotal?.text.toString()
                    val gstCharge = tvGstCharge?.text.toString()
                    val total = tvTotal?.text.toString()

                    // create a bundle to hold the selected products and sent it via extras
                    val bundle = Bundle()
                    bundle.putParcelableArrayList("selectedProducts", selectedProducts)
                    intent.putExtras(bundle)

                    // Put the selected products, customer name, and phone as extra in the intent
                    intent.putExtra("customerName", customerName)
                    intent.putExtra("customerPhone", customerPhone)
                    intent.putExtra("subTotal", subTotal)
                    intent.putExtra("gstCharge", gstCharge)
                    intent.putExtra("total", total)
                    intent.putExtra("selectedAddressString", selectedAddressString)


                    // start the OrderSummaryActivity
                    startActivity(intent)
                }
            }
        }
    }

    private fun updateTotal() {
        var subTotal = 0.0
        var gstCharge = 0.0
        var totalAmount = 0.0

        // Iterate through all the inflated layouts and calculate the total price
        for (i in 0 until llContainerLayout.childCount) {
            val inflatedLayout = llContainerLayout.getChildAt(i)
            val quantityEditText =
                inflatedLayout.findViewById<EditText>(R.id.et_enter_cart_quantity)
            val selectedProduct =
                inflatedLayout.findViewById<Spinner>(R.id.spinner_cart_product_title).selectedItem as? Product
            val quantity = quantityEditText.text.toString().toIntOrNull() ?: 0
            val price = selectedProduct?.sell_price?.toDouble()
            subTotal += price?.times(quantity) ?: 0.0
            gstCharge = 0.05.times(subTotal)
            totalAmount = subTotal + gstCharge

        }
        tvSubTotal?.text = subTotal.toString()
        tvGstCharge?.text = gstCharge.toString()
        tvTotal?.text = totalAmount.toString()
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolBarSelectCustomerActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolBarSelectCustomerActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun successAddressForSelectedCustomer(customerAddressList: java.util.ArrayList<Address>) {
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
}