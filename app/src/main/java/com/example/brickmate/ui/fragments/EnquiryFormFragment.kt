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
import com.google.android.material.bottomsheet.BottomSheetDialog
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
            showAddProductBottomSheet()
        }

        binding.btnSubmitEnquiry.setOnClickListener {
            //val selectedProducts = adapter.getSelectedProducts()
            val name = binding.etEnquiryCustomerName.text.toString().trim()
            val phone = binding.etEnquiryCustomerPhone.text.toString().trim()
            val location = binding.etEnquiryCustomerLocation.text.toString().trim()
            val additionalInfo = binding.etEnquiryAdditionalInfo.text.toString().trim()
            val enquiryDate = generateEnquiryDate()
            if (name.isNotEmpty() && phone.isNotEmpty() && location.isNotEmpty() && productItemList.size > 0) {
                showProgressDialog(resources.getString(R.string.please_wait))
                val enquiry =
                    Enquiry("", enquiryDate, name, phone, location, additionalInfo, productItemList)
                FireStoreClass().saveEnquiry(this, enquiry)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please fill in the required details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showAddProductBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView =
            LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_add_product, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        // initialize views
        val spinner = bottomSheetView.findViewById<Spinner>(R.id.spinner_bottom_sheet_product_title)
        val etQuantity =
            bottomSheetView.findViewById<EditText>(R.id.et_bottom_sheet_product_quantity)
        val etGST = bottomSheetView.findViewById<EditText>(R.id.et_bottom_sheet_product_gst)
        val tvPrice = bottomSheetView.findViewById<TextView>(R.id.tv_dialog_product_price)
        val tvUnit = bottomSheetView.findViewById<TextView>(R.id.tv_dialog_product_uom)
        val checkboxGst = bottomSheetView.findViewById<CheckBox>(R.id.cb_bottom_sheet_apply_gst)

        // set up the spinner with the list of products
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            productsList.map { it.name }
        )
        spinner.adapter = adapter

        // set up the spinner onItemSelected listener to update the price and unit
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedProduct = productsList[position]
                tvPrice.text = "Rs ${selectedProduct.sell_price}"
                tvUnit.text = selectedProduct.uom
                etGST.setText(selectedProduct.gst_rate)
                // Update the product total when the spinner selection changes

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        // Get positive button view
        val positiveButton = bottomSheetView.findViewById<Button>(R.id.btn_bottom_sheet_add)
        val cancelButton = bottomSheetView.findViewById<Button>(R.id.btn_bottom_sheet_cancel)

        // Disable the positive button initially
        positiveButton.isEnabled = false

        // Add a TextChangedListener to the quantity EditText
        etQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val quantity = s.toString().toIntOrNull()
                val gst = etGST.text.toString().toIntOrNull()

                // Enable the positive button if both quantity and gst are greater than 0
                positiveButton.isEnabled =
                    (quantity != null && quantity > 0 && gst != null && gst > 0)

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        // Add a TextChangedListener to the quantity EditText
        etGST.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val quantity = etQuantity.text.toString().toIntOrNull()
                val gst = s.toString().toIntOrNull()

                // Enable the positive button if both quantity and gst are greater than 0
                positiveButton.isEnabled =
                    (quantity != null && quantity > 0 && gst != null && gst > 0)
                // Update the product total when the checkbox state changes
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        // Add a listener to the "Apply GST" checkbox
        checkboxGst.setOnCheckedChangeListener { _, _ ->
            val quantity = etQuantity.text.toString().toIntOrNull()
            val gst = etGST.text.toString().toIntOrNull()

        }
        // Set the positive button's OnClickListener to add the product to the list
        positiveButton.setOnClickListener {
            val selectedPosition = spinner.selectedItemPosition
            val selectedProduct = getCurrentSelectedProduct(selectedPosition)
            val quantity = etQuantity.text.toString().toInt()
            val gstIncluded = checkboxGst.isChecked
            val gst = if (gstIncluded) {
                etGST.text.toString()
            } else {
                ""
            }

            val defaultProduct = Product(
                FireStoreClass().getCurrentUserID(),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                0,
                false,
                false
            )

            val newProductItem = Product(
                FireStoreClass().getCurrentUserID(),
                selectedProduct.product_id,
                selectedProduct.name,
                selectedProduct.description,
                selectedProduct.uom,
                selectedProduct.sell_price,
                gst,
                selectedProduct.product_image,
                quantity,
                isSelected = false,
                gstIncluded = gstIncluded
            )
            productItemList.add(newProductItem)
            updateProductRecView()
            calculateTotalAndUpdateUI()

            bottomSheetDialog.dismiss()
        }
        cancelButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun updateProductRecView() {
        if (productItemList.size > 0) {
            binding.clProductsTotal.visibility = View.VISIBLE
            binding.rvSelectedProducts.visibility = View.VISIBLE
            val adapter = ProductEnquiryAdapter(requireContext(), productItemList)
            adapter.setOnDeleteClickListener(this)
            binding.rvSelectedProducts.layoutManager = LinearLayoutManager(context)
            binding.rvSelectedProducts.adapter = adapter
        } else {
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
        if (binding.clProductsTotal.visibility != View.VISIBLE) {
            binding.clProductsTotal.visibility = View.VISIBLE
        }
        if (productItemList.size == 0) {
            binding.clProductsTotal.visibility = View.GONE
        }
        binding.tvProductTotal.text = "Rs ${"%.2f".format(total)}"
    }

    private fun getCurrentSelectedProduct(position: Int): Product {
        return productsList.getOrNull(position) ?: Product(
            FireStoreClass().getCurrentUserID(),
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            0,
            false,
            false
        )
    }
}