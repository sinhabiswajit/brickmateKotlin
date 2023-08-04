package com.example.brickmate.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityAddCompanyBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Company
import com.example.brickmate.model.Product
import com.example.brickmate.util.Constants
import com.example.brickmate.util.GlideLoader
import java.io.IOException

class AddCompanyActivity : BaseActivity() {
    private var binding: ActivityAddCompanyBinding? = null
    private var toolBarAddCompanyActivity: androidx.appcompat.widget.Toolbar? = null
    private var mSelectedImageFileURI: Uri? = null
    private var company: Company? = null
    private var mCompanyLogoImageURL: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCompanyBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initializeVars()
        setUpActionBar()

        company = intent.getParcelableExtra("company")

        // Initialize your input fields and set their values from the company object
        initializeInputFields(company)

        binding!!.ivCompanyLogoEditIcon.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this@AddCompanyActivity)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding?.btnAddCompany?.setOnClickListener {
            if (validateCompanyDetails()) {
                if (mSelectedImageFileURI != null) {
                    uploadCompanyLogo()
                } else {
                    updateCompanyDetailsToCloud()
                }
            }
        }
    }

    private fun initializeInputFields(company: Company?) {
        if (company != null) {
            binding?.etCompanyName?.setText(company.companyName)
            binding?.etCompanyAddress?.setText(company.address)
            binding?.etCompanyCity?.setText(company.city)
            binding?.etCompanyState?.setText(company.state)
            binding?.etCompanyPinCode?.setText(company.pinCode)
            binding?.btnAddCompany?.text = "UPDATE"
            // Load the company logo using Glide
            binding?.ivCompanyLogo?.let {
                Glide.with(this)
                    .load(company.companyLogo)
                    .placeholder(R.drawable.iv_company_logo_here)
                    .into(it)
            }
        }
    }

    private fun uploadCompanyLogo() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().uploadImageToCloudStorage(
            this,
            mSelectedImageFileURI,
            Constants.COMPANY_LOGO
        )
    }

    private fun validateCompanyDetails(): Boolean {
        val isUpdating = (company != null)
        if (isUpdating) {
            if (binding?.etCompanyName!!.text.isEmpty()) {
                binding?.etCompanyName?.error =
                    resources.getString(R.string.err_msg_enter_company_name)
                binding?.etCompanyName?.requestFocus()
                return false
            }
            if (binding?.etCompanyAddress!!.text.isEmpty()) {
                binding?.etCompanyAddress?.error =
                    resources.getString(R.string.err_msg_enter_company_address)
                binding?.etCompanyAddress?.requestFocus()
                return false
            }
            if (binding?.etCompanyCity!!.text.isEmpty()) {
                binding?.etCompanyCity?.error =
                    resources.getString(R.string.err_msg_enter_company_city)
                binding?.etCompanyCity?.requestFocus()
                return false
            }
            if (binding?.etCompanyState!!.text.isEmpty()) {
                binding?.etCompanyState?.error =
                    resources.getString(R.string.err_msg_enter_company_state)
                binding?.etCompanyState?.requestFocus()
                return false
            }
            if (binding?.etCompanyPinCode!!.text.isEmpty()) {
                binding?.etCompanyPinCode?.error =
                    resources.getString(R.string.err_msg_enter_company_pin_code)
                binding?.etCompanyPinCode?.requestFocus()
                return false
            }
        }
        if (!isUpdating) {
            if (mSelectedImageFileURI == null) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_select_company_logo),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            if (binding?.etCompanyName!!.text.isEmpty()) {
                binding?.etCompanyName?.error =
                    resources.getString(R.string.err_msg_enter_company_name)
                binding?.etCompanyName?.requestFocus()
                return false
            }
            if (binding?.etCompanyAddress!!.text.isEmpty()) {
                binding?.etCompanyAddress?.error =
                    resources.getString(R.string.err_msg_enter_company_address)
                binding?.etCompanyAddress?.requestFocus()
                return false
            }
            if (binding?.etCompanyCity!!.text.isEmpty()) {
                binding?.etCompanyCity?.error =
                    resources.getString(R.string.err_msg_enter_company_city)
                binding?.etCompanyCity?.requestFocus()
                return false
            }
            if (binding?.etCompanyState!!.text.isEmpty()) {
                binding?.etCompanyState?.error =
                    resources.getString(R.string.err_msg_enter_company_state)
                binding?.etCompanyState?.requestFocus()
                return false
            }
            if (binding?.etCompanyPinCode!!.text.isEmpty()) {
                binding?.etCompanyPinCode?.error =
                    resources.getString(R.string.err_msg_enter_company_pin_code)
                binding?.etCompanyPinCode?.requestFocus()
                return false
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            if (data != null) {
                binding?.ivCompanyLogoEditIcon?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_vector_edit
                    )
                )
                try {
                    mSelectedImageFileURI = data.data!!
                    GlideLoader(this).loadCompanyLogo(
                        mSelectedImageFileURI!!,
                        binding?.ivCompanyLogo!!
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        resources.getString(R.string.image_selection_failed),
                        Toast.LENGTH_LONG
                    ).show()
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
                Constants.showImageChooser(this@AddCompanyActivity)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initializeVars() {
        toolBarAddCompanyActivity = binding?.toolbarAddCompanyActivity
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolBarAddCompanyActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_light)
        }
        toolBarAddCompanyActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun imageUploadSuccess(imageURL: String) {
        hideProgressDialog()
        mCompanyLogoImageURL = imageURL
        if (company != null) {
            updateCompanyDetailsToCloud()
        } else {
            uploadCompanyDetailsToCloud()
        }
    }

    private fun updateCompanyDetailsToCloud() {
        var imageURL = ""
        imageURL = mCompanyLogoImageURL.ifEmpty {
            company!!.companyLogo
        }
        val updatedCompany = hashMapOf(
            "companyName" to binding?.etCompanyName?.text.toString().trim { it <= ' ' },
            "address" to binding?.etCompanyAddress?.text.toString().trim { it <= ' ' },
            "city" to binding?.etCompanyCity?.text.toString().trim { it <= ' ' },
            "state" to binding?.etCompanyState?.text.toString().trim { it <= ' ' },
            "pinCode" to binding?.etCompanyPinCode?.text.toString().trim { it <= ' ' },
            "companyLogo" to imageURL
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().updateCompany(this, updatedCompany)
    }

    private fun uploadCompanyDetailsToCloud() {
        val company = Company(
            FireStoreClass().getCurrentUserID(),
            binding?.etCompanyName?.text.toString().trim { it <= ' ' },
            mCompanyLogoImageURL,
            binding?.etCompanyAddress?.text.toString().trim { it <= ' ' },
            binding?.etCompanyCity?.text.toString().trim { it <= ' ' },
            binding?.etCompanyState?.text.toString().trim { it <= ' ' },
            binding?.etCompanyPinCode?.text.toString().trim { it <= ' ' },
            true
        )
        FireStoreClass().uploadCompanyDetails(this, company)
    }

    fun companyUploadSuccessToCloud() {
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.success_msg_upload_company),
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    fun companyUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Company details updated successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
}