package com.example.brickmate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Customer

class AddCustomerActivity : BaseActivity() {

    private var toolBarAddNewCustomerActivity: Toolbar? = null
    private var btnAddNewCustomer: Button? = null
    private var firstName: EditText? = null
    private var lastName: EditText? = null
    private var phone: EditText? = null
    private var email: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)
        initializeVars()
        setUpActionBar()
        btnAddNewCustomer?.setOnClickListener {
            saveCustomerToFireStore()
        }
    }


    private fun saveCustomerToFireStore() {
        val name = firstName?.text.toString().trim { it <= ' ' } + " " + lastName?.text.toString().trim { it <= ' ' }
        val phone = phone?.text.toString().trim { it <= ' ' }
        val email = email?.text.toString().trim { it <= ' ' }
        if (validateData()){
            showProgressDialog(resources.getString(R.string.please_wait))
            val customerModel = Customer("", name, phone, email)
            FireStoreClass().addCustomer(this@AddCustomerActivity, customerModel)
        }
    }

    private fun initializeVars() {
        toolBarAddNewCustomerActivity = findViewById(R.id.toolbar_add_customer_activity)
        btnAddNewCustomer = findViewById(R.id.btn_add_new_customer)
        firstName = findViewById(R.id.et_customer_firstname)
        lastName = findViewById(R.id.et_customer_lastname)
        phone = findViewById(R.id.et_customer_phone)
        email = findViewById(R.id.et_customer_email)
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolBarAddNewCustomerActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolBarAddNewCustomerActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(firstName?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddCustomerActivity,
                    resources.getString(R.string.err_msg_enter_first_name),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(lastName?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddCustomerActivity,
                    resources.getString(R.string.err_msg_enter_last_name),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(phone?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddCustomerActivity,
                    resources.getString(R.string.err_msg_enter_phone),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(email?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddCustomerActivity,
                    resources.getString(R.string.err_msg_enter_email),
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
        Toast.makeText(this,"Customer added successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
}