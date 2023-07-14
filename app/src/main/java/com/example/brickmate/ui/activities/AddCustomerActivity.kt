package com.example.brickmate.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityAddCustomerBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Address
import com.example.brickmate.model.Customer
import java.util.*

class AddCustomerActivity : BaseActivity() {

    private var binding: ActivityAddCustomerBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()

        binding?.btnAddNewCustomer?.setOnClickListener {
            saveCustomerToFireStore()
        }
    }


    private fun saveCustomerToFireStore() {
        val name = binding?.etCustomerFirstname?.text.toString().trim { it <= ' ' }.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } + " " +
                binding?.etCustomerLastname?.text.toString().trim { it <= ' ' }.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        val phone = binding?.etCustomerPhone?.text.toString().trim { it <= ' ' }
        val email = binding?.etCustomerEmail?.text.toString().trim { it <= ' ' }
        val gstin = binding?.etCustomerGstin?.text.toString().trim { it <= ' ' }
        val address = binding?.etCustomerAddress?.text.toString().trim { it <= ' ' }
        val zipcode = binding?.etCustomerZipcode?.text.toString().trim { it <= ' ' }
        val landmark = binding?.etCustomerLandmark?.text.toString().trim { it <= ' ' }
        val siteContact = binding?.etCustomerSitePersonContact?.text.toString().trim { it <= ' ' }

        if (validateData()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val customerModel = Customer(FireStoreClass().getCurrentUserID(),"", name, phone, email, gstin, arrayListOf(Address(address,zipcode, landmark)), siteContact)
            FireStoreClass().addCustomer(this@AddCustomerActivity, customerModel)
        }
    }

    private fun setUpActionBar() {
        val toolbar = binding?.toolbarAddCustomerActivity
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolbar?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(binding?.etCustomerFirstname?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddCustomerActivity,
                    resources.getString(R.string.err_msg_enter_first_name),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(binding?.etCustomerLastname?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddCustomerActivity,
                    resources.getString(R.string.err_msg_enter_last_name),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(binding?.etCustomerPhone?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddCustomerActivity,
                    resources.getString(R.string.err_msg_enter_phone),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(binding?.etCustomerAddress?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddCustomerActivity,
                    resources.getString(R.string.err_msg_enter_address),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(binding?.etCustomerZipcode?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddCustomerActivity,
                    resources.getString(R.string.err_msg_enter_zipcode),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(binding?.etCustomerLandmark?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddCustomerActivity,
                    resources.getString(R.string.err_msg_enter_landmark),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }

        }
    }

    fun successAddCustomerListFromFireStore() {
        hideProgressDialog()
        Toast.makeText(this, "Customer added successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}