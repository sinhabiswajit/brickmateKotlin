package com.example.brickmate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Address

class AddAddressActivity : BaseActivity() {
    private var toolBarAddAddressActivity : androidx.appcompat.widget.Toolbar? = null
    private var etCustomerName : EditText? = null
    private var etCustomerPhone : EditText? = null
    private var etCustomerAddress : EditText? = null
    private var etCustomerZipcode : EditText? = null
    private var etCustomerLandmark : EditText? = null
    private var btnAddAddress : Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)
        initializeVars()
        setUpActionBar()

        if (intent.hasExtra("name")){
            val name = intent.getStringExtra("name")
            etCustomerName?.setText(name)
            etCustomerName?.isEnabled = false
            val phone = intent.getStringExtra("phone")
            etCustomerPhone?.setText(phone)
            etCustomerPhone?.isEnabled = false
        }
        btnAddAddress?.setOnClickListener {
                saveCustomerAddressToFireStore()
        }
    }

    private fun saveCustomerAddressToFireStore() {

        if (validateAddressDetails()){
            showProgressDialog(resources.getString(R.string.please_wait))
            val name = etCustomerName?.text.toString().trim { it <= ' ' }
            val phone = etCustomerPhone?.text.toString().trim { it <= ' ' }
            val address = etCustomerAddress?.text.toString().trim { it <= ' ' }
            val zipcode = etCustomerZipcode?.text.toString().trim { it <= ' ' }
            val landmark = etCustomerLandmark?.text.toString().trim { it <= ' ' }
            val addressModel = Address(address,zipcode,landmark)

            FireStoreClass().uploadCustomerAddressToFireStore(this@AddAddressActivity, addressModel, name)
        }

    }

    private fun initializeVars() {
        toolBarAddAddressActivity = findViewById(R.id.toolbar_add_address_activity)
        etCustomerName = findViewById(R.id.et_add_address_full_name)
        etCustomerPhone = findViewById(R.id.et_add_address_phone_number)
        etCustomerAddress = findViewById(R.id.et_add_address_address)
        etCustomerZipcode = findViewById(R.id.et_add_address_zip_code)
        etCustomerLandmark = findViewById(R.id.et_add_address_landmark)
        btnAddAddress = findViewById(R.id.btn_submit_address)
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolBarAddAddressActivity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolBarAddAddressActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateAddressDetails(): Boolean {
        return when{
            TextUtils.isEmpty(etCustomerAddress?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, resources.getString(R.string.err_msg_enter_address), Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etCustomerZipcode?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, resources.getString(R.string.err_msg_enter_zipcode), Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etCustomerLandmark?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, resources.getString(R.string.err_msg_enter_landmark), Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

    fun customerAddressUploadSuccess() {
        hideProgressDialog()
        Toast.makeText(this@AddAddressActivity, "Address added successfully", Toast.LENGTH_SHORT).show()
        finish()
    }


}