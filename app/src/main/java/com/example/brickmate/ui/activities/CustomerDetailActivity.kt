package com.example.brickmate.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityCustomerDetailBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Address
import com.example.brickmate.model.Customer
import com.example.brickmate.ui.adapters.CustomerAddressAdapter
import com.example.brickmate.util.Constants

class CustomerDetailActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCustomerDetailBinding
    private lateinit var customerDetails : Customer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        customerDetails = Customer()
        customerDetails = intent.getParcelableExtra<Customer>("customer_model")!!
        getCustomerDetails()

        binding.ivCustomerCall.setOnClickListener(this)
        binding.ivCustomerMessage.setOnClickListener(this)
        binding.ivCustomerEmail.setOnClickListener(this)
        binding.btnCustomerDelete.setOnClickListener(this)
        binding.btnCustomerUpdate.setOnClickListener(this)

    }

    private fun getCustomerDetails() {
        if (customerDetails!=null) {
            binding.tvCustomerName.text = customerDetails.name
            binding.tvCustomerPhone.text = "+91 ${customerDetails.phone}"
            if (TextUtils.isEmpty(customerDetails.email)){
                binding.tvCustomerEmail.text = "N/A"
            }else{
                binding.tvCustomerEmail.text = customerDetails.email
            }
            if (TextUtils.isEmpty(customerDetails.gstin)){
                binding.tvCustomerGstinNo.text = "N/A"
            }
            else{
                binding.tvCustomerGstinNo.text = customerDetails.gstin
            }
            val adapter = CustomerAddressAdapter(this, customerDetails.addresses as ArrayList<Address>)
            binding.rvCustomerAddresses.visibility = View.VISIBLE
            binding.rvCustomerAddresses.layoutManager = LinearLayoutManager(this)
            binding.rvCustomerAddresses.adapter = adapter
            if (TextUtils.isEmpty(customerDetails.siteContactPerson)){
                binding.tvSitePersonContact.text = "N/A"
            }else{
                binding.tvSitePersonContact.text = customerDetails.siteContactPerson
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_UPDATE_CUSTOMER && resultCode == Activity.RESULT_OK) {
            // Retrieve the updated customer from the result intent
            val updatedCustomer = data?.getParcelableExtra<Customer>("updated_customer")
            if (updatedCustomer != null) {
                // Update the customer details object with the updated customer
                customerDetails.apply {
                    customerDetails.name = updatedCustomer.name
                    customerDetails.phone = updatedCustomer.phone
                    customerDetails.email = updatedCustomer.email
                    customerDetails.gstin = updatedCustomer.gstin
                    customerDetails.addresses = updatedCustomer.addresses.toMutableList()
                    customerDetails.siteContactPerson = updatedCustomer.siteContactPerson
                }
                // Refresh the data displayed on the activity
                getCustomerDetails()
            }
        }
    }

    private fun setUpActionBar() {
        val toolbar = binding.toolbarCustomerDetailActivity
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_customer_call -> {
                callCustomer()
            }
            R.id.iv_customer_message -> {
                messageCustomer()
            }
            R.id.iv_customer_email -> {
                if (TextUtils.isEmpty(customerDetails.email) || customerDetails.email == "N/A"){
                    Toast.makeText(this,"No mail found!", Toast.LENGTH_LONG).show()
                    binding.ivCustomerEmail.isEnabled = false
                    binding.ivCustomerEmail.setColorFilter(ContextCompat.getColor(this, R.color.color_light_grey), PorterDuff.Mode.SRC_IN)
                }else{
                    mailCustomer()
                }
            }
            R.id.btn_customer_delete -> {
                deleteCustomer()
            }
            R.id.btn_customer_update -> {
                val intent = Intent(this, CustomerDetailUpdateActivity::class.java)
                intent.putExtra("customer", customerDetails)
                startActivityForResult(intent,Constants.REQUEST_CODE_UPDATE_CUSTOMER)
            }
        }
    }

    private fun mailCustomer() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(customerDetails.email))
        intent.putExtra(Intent.EXTRA_SUBJECT, "BrickMate : []")
        intent.putExtra(Intent.EXTRA_TEXT, "")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Send email"))
        }
    }

    private fun messageCustomer() {
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${customerDetails.phone}")
        }
        startActivity(Intent.createChooser(smsIntent, "Select Messaging App"))
    }

    private fun callCustomer() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Call Customer")
        builder.setMessage("Are you sure you want to call this customer?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", customerDetails.phone, null))
            startActivity(intent)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deleteCustomer() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Customer")
        builder.setMessage("Are you sure you want to delete this Customer?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().deleteCustomerFromFireStore(this, customerDetails.customer_id)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun successDeleteCustomer() {
        hideProgressDialog()
        Toast.makeText(this, "Customer deleted successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}