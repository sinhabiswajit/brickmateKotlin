package com.example.brickmate.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityCustomerDetailUpdateBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Address
import com.example.brickmate.model.Customer
import com.example.brickmate.ui.adapters.CustomerAddressUpdateAdapter
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class CustomerDetailUpdateActivity : BaseActivity(),
    CustomerAddressUpdateAdapter.OnDeleteClickListener {

    private lateinit var binding: ActivityCustomerDetailUpdateBinding
    private lateinit var customerDetails: Customer
    private var addressItemList: ArrayList<Address> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDetailUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        customerDetails = intent.getParcelableExtra<Customer>("customer")!!
        setUpUI()
        binding.btnCustomerUpdate.setOnClickListener {
            updateCustomerDetails()
        }
        binding.tvAddAddress.setOnClickListener {
            addAddress()
        }
    }

    private fun addAddress() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.item_add_address, null)

        val etAddress = view.findViewById<EditText>(R.id.et_dialog_add_address)
        val etZipcode = view.findViewById<EditText>(R.id.et_dialog_add_zipcode)
        val etLandmark = view.findViewById<EditText>(R.id.et_dialog_add_landmark)

        builder.setView(view)
        builder.setPositiveButton("Add") { dialog, which ->
            val address = etAddress.text.toString()
            val zipcode = etZipcode.text.toString()
            val landmark = etLandmark.text.toString()
            if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(zipcode) && !TextUtils.isEmpty(landmark)){
                val newAddress = Address(address, zipcode, landmark)
                addressItemList.add(newAddress)
                updateAddressRecView()
                dialog.dismiss()
            }else{

            }

        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun updateCustomerDetails() {
        val name = binding.etCustomerName.text.toString().trim().split(" ")
            .joinToString(" ") { it -> it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } }
        val phone = binding.etCustomerPhone.text.toString().trim()
        val email = binding.etCustomerEmail.text.toString().trim()
        val gstin = binding.etCustomerGstinNo.text.toString().trim()
        val siteContact = binding.etSitePersonContact.text.toString().trim()
        if (addressItemList.size > 0 && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
            val updatedCustomer = hashMapOf(
                "customer_id" to customerDetails.customer_id,
                "name" to name,
                "phone" to phone,
                "email" to email,
                "gstin" to gstin,
                "addresses" to addressItemList,
                "siteContactPerson" to siteContact
            )
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().updateCustomerDetail(
                this,
                updatedCustomer,
                customerDetails.customer_id
            )
        } else {
            Toast.makeText(
                this,
                "Please add mandatory fields(name, phone, address)!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setUpUI() {
        addressItemList = customerDetails.addresses as ArrayList<Address>
        updateAddressRecView()
        binding.etCustomerName.setText(customerDetails.name)
        binding.etCustomerPhone.setText(customerDetails.phone)
        if (TextUtils.isEmpty(customerDetails.email)) {
            binding.etCustomerEmail.setText("N/A")
        } else {
            binding.etCustomerEmail.setText(customerDetails.email)
        }
        if (TextUtils.isEmpty(customerDetails.gstin)) {
            binding.etCustomerGstinNo.setText("N/A")
        } else {
            binding.etCustomerGstinNo.setText(customerDetails.gstin)
        }
        if (TextUtils.isEmpty(customerDetails.siteContactPerson)) {
            binding.etSitePersonContact.setText("N/A")
        } else {
            binding.etSitePersonContact.setText(customerDetails.siteContactPerson)
        }
    }

    private fun setUpActionBar() {
        val toolbar = binding.toolbarCustomerDetailUpdateActivity
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

    override fun onDeleteClick(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Remove Address")
        builder.setMessage("Are you sure you want to remove this address for this customer?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            addressItemList.removeAt(position)
            updateAddressRecView()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun updateAddressRecView() {
        val adapter = CustomerAddressUpdateAdapter(this, addressItemList)
        adapter.setOnDeleteClickListener(this)
        binding.rvCustomerAddresses.layoutManager = LinearLayoutManager(this)
        binding.rvCustomerAddresses.adapter = adapter
    }

    fun successUpdateCustomer(customer: HashMap<String, Serializable>) {
        val updatedCustomer = Customer(
            customer_id = customer["customer_id"] as String,
            name = customer["name"] as String,
            phone = customer["phone"] as String,
            email = customer["email"] as String,
            gstin = customer["gstin"] as String,
            addresses = customer["addresses"] as List<Address>,
            siteContactPerson = customer["siteContactPerson"] as String
        )
        customerDetails = updatedCustomer
        hideProgressDialog()
        setResult(Activity.RESULT_OK, Intent().putExtra("updated_customer", updatedCustomer))
        Toast.makeText(this, "Customer updated successfully!", Toast.LENGTH_LONG).show()
        finish()
    }

}