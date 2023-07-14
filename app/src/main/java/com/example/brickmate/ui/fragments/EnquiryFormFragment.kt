package com.example.brickmate.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickmate.R
import com.example.brickmate.databinding.FragmentEnquiryFormBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Enquiry
import com.example.brickmate.model.Product
import com.example.brickmate.ui.adapters.ProductCheckboxAdapter
import com.example.brickmate.ui.adapters.ProductEnquiryAdapter
import kotlin.collections.ArrayList

class EnquiryFormFragment : BaseFragment(), ProductEnquiryAdapter.OnDeleteClickListener {

    private var _binding: FragmentEnquiryFormBinding? = null
    private val binding get() = _binding!!
    private var productsList: ArrayList<Product> = ArrayList()
    private var productItemList: ArrayList<Product> = ArrayList()
    private lateinit var adapter: ProductCheckboxAdapter
    private var total: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnquiryFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAddProduct.setOnClickListener {
            //showAddProductDialog()
        }

        binding.btnSubmitEnquiry.setOnClickListener {
            //val selectedProducts = adapter.getSelectedProducts()
            val name = binding.etEnquiryCustomerName.text.toString().trim()
            val phone = binding.etEnquiryCustomerPhone.text.toString().trim()
            val location = binding.etEnquiryCustomerLocation.text.toString().trim()
            val additionalInfo = binding.etEnquiryAdditionalInfo.text.toString().trim()
            val enquiryDate = generateEnquiryDate()
            if (name.isNotEmpty() && phone.isNotEmpty() && location.isNotEmpty() && productItemList.size>0){
                showProgressDialog(resources.getString(R.string.please_wait))
                val enquiry = Enquiry("", enquiryDate, name, phone, location, additionalInfo, productItemList)
                FireStoreClass().saveEnquiry(this, enquiry)
            }else{
                Toast.makeText(requireContext(), "Please fill in the required details", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun updateProductRecView() {
        if (productItemList.size > 0){
            binding.clProductsTotal.visibility = View.VISIBLE
            binding.rvSelectedProducts.visibility = View.VISIBLE
            val adapter = ProductEnquiryAdapter(requireContext(), productItemList)
            adapter.setOnDeleteClickListener(this)
            binding.rvSelectedProducts.layoutManager = LinearLayoutManager(context)
            binding.rvSelectedProducts.adapter = adapter
        }else{
            binding.clProductsTotal.visibility = View.GONE
            binding.rvSelectedProducts.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }

    private fun getProductList() {
        FireStoreClass().getProductListFromFireStore(this)
    }

    fun successSaveEnquiry() {
        hideProgressDialog()
        Toast.makeText(requireActivity(), "Enquiry Submitted!", Toast.LENGTH_SHORT).show()
        clearForm()
    }

    private fun clearForm() {
        binding.etEnquiryCustomerName.text.clear()
        binding.etEnquiryCustomerPhone.text.clear()
        binding.etEnquiryCustomerLocation.text.clear()
        binding.etEnquiryAdditionalInfo.text.clear()
        productItemList.clear()
        updateProductRecView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun successGetProductList(productsList: ArrayList<Product>) {
        this.productsList = productsList
    }

//    override fun onProductClick(product: Product) {
//        product.isSelected = !product.isSelected
//    }

    override fun onDeleteClick(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Remove Product")
        builder.setMessage("Are you sure you want to remove this product from Order?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            productItemList.removeAt(position)
            updateProductRecView()
            calculateTotalAndUpdateUI()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun calculateTotalAndUpdateUI() {
        total = productItemList.sumByDouble { it.sell_price.toDouble() * it.quantity }
        if (binding.clProductsTotal.visibility != View.VISIBLE){
            binding.clProductsTotal.visibility = View.VISIBLE
        }
        if (productItemList.size == 0){
            binding.clProductsTotal.visibility = View.GONE
        }
        binding.tvProductTotal.text = "Rs $total"
    }

}