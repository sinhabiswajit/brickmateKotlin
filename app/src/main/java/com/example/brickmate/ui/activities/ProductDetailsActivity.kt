package com.example.brickmate.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.CartItem
import com.example.brickmate.model.Product
import com.example.brickmate.util.Constants
import com.example.brickmate.util.GlideLoader
import org.w3c.dom.Text

class ProductDetailsActivity : BaseActivity(), OnClickListener {
    private var toolBarProductDetailsActivity : Toolbar? = null
    private var mProductId: String = ""
    private lateinit var mProductDetails: Product
    private var ivProductDetailsImage : ImageView? = null
    private var tvProductDetailsName : TextView? = null
    private var tvProductDetailsSubTextPrice : TextView? = null
    private var tvProductDetailsSubTextUOM : TextView? = null
    private var tvProductDetailsDescription : TextView? = null
    private var tvProductDetailsUOM : TextView? = null
    private var tvProductDetailsSellPrice : TextView? = null
    private var tvProductDetailsGSTRate : TextView? = null
    private var btnGoToOrder : Button? = null
    private var btnUpdateProduct : Button? = null
    private var btnDeleteProduct : Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        initializeVars()
        setUpActionBar()

        btnUpdateProduct?.setOnClickListener(this)
        btnGoToOrder?.setOnClickListener(this)
        btnDeleteProduct?.setOnClickListener(this)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        FireStoreClass().checkIfItemExistsInCart(this, mProductId)

    }

    override fun onResume() {
        super.onResume()
        getProductDetails()
    }

    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getProductDetails(this, mProductId)
    }

    private fun initializeVars() {
        toolBarProductDetailsActivity = findViewById(R.id.toolbar_product_details_activity)
        ivProductDetailsImage = findViewById(R.id.iv_product_details_image)
        tvProductDetailsName = findViewById(R.id.tv_product_details_title)
        tvProductDetailsSubTextPrice = findViewById(R.id.tv_product_details_sub_text_price)
        tvProductDetailsSubTextUOM = findViewById(R.id.tv_product_details_sub_text_uom)
        tvProductDetailsDescription = findViewById(R.id.tv_product_details_description)
        tvProductDetailsUOM = findViewById(R.id.tv_product_details_unit)
        tvProductDetailsSellPrice = findViewById(R.id.tv_product_details_sell_price)
        tvProductDetailsGSTRate = findViewById(R.id.tv_product_details_gst)
        btnUpdateProduct = findViewById(R.id.btn_update_product)
        btnGoToOrder = findViewById(R.id.btn_go_to_orders)
        btnDeleteProduct = findViewById(R.id.btn_delete_product)
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolBarProductDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolBarProductDetailsActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_update_product -> {
                //addToCart(mProductDetails)
                Log.e("Product", mProductDetails.toString())
                val intent = Intent(this@ProductDetailsActivity, AddProductActivity::class.java)
                intent.putExtra("productId", mProductId)
                intent.putExtra("product", mProductDetails)
                startActivity(intent)


            }
            R.id.btn_go_to_orders -> {
                startActivity(Intent(this@ProductDetailsActivity, OrderActivity::class.java))
            }
            R.id.btn_delete_product -> {
                deleteProduct()

            }
        }
    }

    private fun deleteProduct() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Product")
        builder.setMessage("Are you sure you want to delete this Product?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().deleteProduct(this, mProductId)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun addToCart(product: Product) {
        val cartItem = CartItem(
            mProductId,
            product.name,
            product.sell_price,
            product.uom,
            product.product_image,
            Constants.DEFAULT_PRODUCT_QUANTITY,
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().saveCartListToFireStore(this@ProductDetailsActivity, cartItem)
    }

    fun productDetailsSuccess(product: Product) {
        hideProgressDialog()
        mProductDetails = product
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.product_image,
            ivProductDetailsImage!!
        )
        tvProductDetailsName?.text = product.name
        tvProductDetailsSubTextPrice?.text = "Rs ${product.sell_price}"
        tvProductDetailsSubTextUOM?.text = product.uom
        tvProductDetailsSellPrice?.text = "Rs ${product.sell_price}"
        tvProductDetailsDescription?.text = product.description
        tvProductDetailsUOM?.text = product.uom
        tvProductDetailsGSTRate?.text = product.gst_rate
    }

    fun addToCartSuccess() {
            hideProgressDialog()
            Toast.makeText(this@ProductDetailsActivity,
                resources.getString(R.string.success_message_item_added_to_cart), Toast.LENGTH_LONG).show()

            btnUpdateProduct?.visibility = View.GONE
            btnGoToOrder?.visibility = View.VISIBLE
    }

    fun productExistsInCart() {
        hideProgressDialog()
        btnUpdateProduct?.visibility = View.GONE
        btnGoToOrder?.visibility = View.VISIBLE
    }

    fun successDeleteProduct() {
        hideProgressDialog()
        Toast.makeText(this, "Product deleted successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}