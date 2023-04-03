package com.example.brickmate.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Product
import com.example.brickmate.util.Constants
import com.example.brickmate.util.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity(), OnClickListener {

    private var toolbarAddProductActivity : Toolbar? = null
    private var mSelectedImageFileURI: Uri? = null
    private var mProductImageURL: String = ""

    private var ivProductImage : ImageView? = null
    private var ivEditProductIcon : ImageView? = null
    private var etProductName : EditText? = null
    private var etProductDescription : EditText? = null
    private var etProductUnitOfMeasurement : EditText? = null
    private var etProductSellPrice : EditText? = null
    private var etProductGstRate : EditText? = null
    private var btnAddProduct : Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        initializeVars()
        setUpActionBar()

        ivProductImage?.setOnClickListener(this)
        ivEditProductIcon?.setOnClickListener(this)
        btnAddProduct?.setOnClickListener(this)
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbarAddProductActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolbarAddProductActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initializeVars() {
        toolbarAddProductActivity = findViewById(R.id.toolbar_add_product_activity)
        ivProductImage = findViewById(R.id.iv_product_image)
        ivEditProductIcon = findViewById(R.id.iv_add_update_product)
        etProductName = findViewById(R.id.et_product_name)
        etProductDescription = findViewById(R.id.et_product_description)
        etProductUnitOfMeasurement = findViewById(R.id.et_product_unit)
        etProductSellPrice = findViewById(R.id.et_product_sell_price)
        etProductGstRate = findViewById(R.id.et_product_gst)
        btnAddProduct = findViewById(R.id.btn_add_product)
    }

    override fun onClick(view: View?) {
        when(view?.id){
           R.id.iv_add_update_product -> {
               if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                   Constants.showImageChooser(this@AddProductActivity)
               } else {
                   ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
               }
           }
            R.id.btn_add_product -> {
                if (validateProductDetails()){
                    uploadProductImage()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            // If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddProductActivity)
            } else {
                Toast.makeText(this, resources.getString(R.string.read_storage_permission_denied), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    ivEditProductIcon?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_edit))
                    try {
                        mSelectedImageFileURI = data.data!!
                        GlideLoader(this).loadProductPicture(mSelectedImageFileURI!!, ivProductImage!!)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, resources.getString(R.string.image_selection_failed), Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("REQUEST CANCELLED", "Image selection cancelled")
        }
    }

    private fun uploadProductImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().uploadImageToCloudStorage(
            this,
            mSelectedImageFileURI,
            Constants.PRODUCT_IMAGE
        )
    }

    fun imageUploadSuccess(imageURL: String) {
        hideProgressDialog()
        //showErrorSnackBar("Product image is uploaded successfully. Image URL : $imageURL", false)
        mProductImageURL = imageURL
        uploadProductDetailsToCloud()
    }

    private fun uploadProductDetailsToCloud() {
        val product = Product("",
            etProductName?.text.toString().trim { it <= ' ' },
            etProductDescription?.text.toString().trim { it <= ' ' },
            etProductUnitOfMeasurement?.text.toString().trim { it <= ' ' },
            etProductSellPrice?.text.toString().trim { it <= ' ' },
            etProductGstRate?.text.toString().trim { it <= ' ' },
            mProductImageURL)
        FireStoreClass().uploadProductDetails(this, product)

    }

    private fun validateProductDetails(): Boolean {
        return when {
            mSelectedImageFileURI == null -> {
                Toast.makeText(this, resources.getString(R.string.err_msg_select_product_image), Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etProductName?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, resources.getString(R.string.err_msg_enter_product_title), Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etProductDescription?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this,resources.getString(R.string.err_msg_enter_product_description), Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etProductSellPrice?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, resources.getString(R.string.err_msg_enter_product_price),Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etProductUnitOfMeasurement?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this,resources.getString(R.string.err_msg_enter_product_uom),Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(etProductGstRate?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this,resources.getString(R.string.err_msg_enter_product_gst_rate),Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }

    fun productUploadSuccessToCloud() {
        hideProgressDialog()
        Toast.makeText(this, resources.getString(R.string.product_upload_success_msg), Toast.LENGTH_LONG).show()
        finish()
    }
}