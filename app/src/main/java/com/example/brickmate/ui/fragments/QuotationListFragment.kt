package com.example.brickmate.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.FragmentEnquiryListBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Quotation
import com.example.brickmate.model.Product
import com.example.brickmate.ui.adapters.QuotationAdapter
import java.util.ArrayList

class QuotationListFragment : BaseFragment() {
    private var _binding: FragmentEnquiryListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_enquiry_list, container, false)
        _binding = FragmentEnquiryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getEnquiryList()
    }

    private fun getEnquiryList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getEnquiryList(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun successGetEnquiryList(quotationList: ArrayList<Quotation>) {
        hideProgressDialog()
        if (quotationList.size > 0) {
            binding.tvNoQuotationFound.visibility = View.GONE
            binding.rvQuotationList.visibility = View.VISIBLE
            binding.rvQuotationList.layoutManager = LinearLayoutManager(context)
            binding.rvQuotationList.setHasFixedSize(true)
            val adapter = QuotationAdapter(requireContext(), quotationList, this)
            binding.rvQuotationList.adapter = adapter
        } else {
            binding.tvNoQuotationFound.visibility = View.VISIBLE
            binding.rvQuotationList.visibility = View.GONE
        }
    }

    fun deleteEnquiry(enquiryId: String) {
        showAlertDialogDeleteEnquiry(enquiryId)
    }

    private fun showAlertDialogDeleteEnquiry(enquiryId: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Delete Enquiry")
        builder.setMessage("Are you sure you want to delete this enquiry?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().deleteEnquiryFromFireStore(this, enquiryId)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun successDeleteEnquiry() {
        hideProgressDialog()
        Toast.makeText(requireActivity(), "Enquiry successfully deleted", Toast.LENGTH_SHORT).show()
        getEnquiryList()

    }

    fun callEnquiry(phone: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Call Customer")
        builder.setMessage("Are you sure you want to call this customer?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
            startActivity(intent)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    fun messageEnquiry(products: List<Product>, phone: String, enquiryDate: String) {
        //Toast.makeText(requireActivity(), "Implementation Pending!", Toast.LENGTH_SHORT).show()
        val products = products.joinToString(separator = ", ") { it.name }
        val message =
            "Hi,\n\nYou called us regarding $products on\n${enquiryDate}.\nGIVE US A CALL FOR FURTHER PROCESS.\n\nThanks & Regards"
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phone")
            putExtra("sms_body", message)
        }

        startActivity(Intent.createChooser(smsIntent, "Select Messaging App"))
    }
}