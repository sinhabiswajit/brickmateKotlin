package com.example.brickmate.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityViewQuotationBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Company
import com.example.brickmate.model.Product
import com.example.brickmate.model.Quotation
import com.example.brickmate.ui.adapters.ItemProductQuotationAdapter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class ViewQuotationActivity : BaseActivity() {
    private lateinit var container: LinearLayout
    private lateinit var quotation : Quotation
    private var binding: ActivityViewQuotationBinding? = null
    private var productsList: ArrayList<Product> = ArrayList()
    private lateinit var scrollView: ScrollView
    private var toolBarViewQuotationActivity: androidx.appcompat.widget.Toolbar? = null
    private var selectedDocumentUri: Uri? = null

    private val pdfPermissionRequestCode = 456
    private val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewQuotationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initializeVars()
        setUpActionBar()
        setUpActionBar()
        quotation = Quotation()
        quotation = intent.getParcelableExtra<Quotation>("quotation_model")!!
        getQuotationDetails()
        getCompanyDetails()
        binding?.btnDownloadQuotation?.setOnClickListener {
            requestPdfPermission()
        }
    }

    private fun requestPdfPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted, generate and save the PDF
            generateAndSavePdf()
        } else {
            // Request storage permission using Android's permission system
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                pdfPermissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == pdfPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, generate and save the PDF
                generateAndSavePdf()
            } else {
                showToast("Storage permission denied")
            }
        }
    }

    private fun getCompanyDetails() {
        FireStoreClass().getCompanyDetailsFromFireStore(this)
    }

    private fun getQuotationDetails() {
        productsList = quotation.productList as ArrayList<Product>
        binding?.rvProductQuotation?.layoutManager = LinearLayoutManager(this)
        binding?.rvProductQuotation?.adapter = ItemProductQuotationAdapter(this, productsList)
        binding?.tvQuotationCustomerName?.text = quotation.name
        binding?.tvQuotationCustomerLocation?.text = quotation.location
        binding?.tvQuotationCustomerPhone?.text = quotation.phone
        binding?.tvQuotationDate?.text = "Date : " + formatValidDate(quotation.quotation_date)
        binding?.tvQuotationValidUntil?.text = "Valid Until : " + formatValidDate(quotation.quotation_valid_date)
    }

    private fun initializeVars() {
        toolBarViewQuotationActivity = binding?.toolbarViewQuotationActivity
        scrollView = binding?.scrollviewQuotation!!
        container = binding?.container!!
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolBarViewQuotationActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_light)
        }
        toolBarViewQuotationActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun successGetCompanyDetailsFromFireStore(company: Company) {
        if (company!=null){
            binding?.tvQuotationCompanyName?.text = company.companyName
            binding?.tvQuotationCompanyAddress?.text = company.address
            binding?.tvQuotationCompanyCityState?.text = company.city+","+company.state
            binding?.tvQuotationCompanyPinCode?.text = company.pinCode
            binding?.tvQuotationCompanyLogo?.let {
                Glide.with(this)
                    .load(company.companyLogo)
                    .placeholder(R.drawable.iv_company_logo_here)
                    .into(it)
            }
        }
    }

    private fun generateAndSavePdf() {
        val currentTimeMillis = System.currentTimeMillis()
        val pdfFileName = "quotation_$currentTimeMillis.pdf"
        val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val pdfFile = File(downloadDirectory, pdfFileName)
        val document = com.itextpdf.text.Document()

        try {
            PdfWriter.getInstance(document, FileOutputStream(pdfFile))
            document.open()

            // Create a ByteArrayOutputStream to capture the view as an image
            val stream = ByteArrayOutputStream()
            val bitmap = getBitmapFromView(container)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = Image.getInstance(stream.toByteArray())

            // Set image dimensions as per your requirements
            image.scaleToFit(document.pageSize.width, document.pageSize.height)
            document.add(image)
            document.close()

            showToast("PDF saved to ${pdfFile.absolutePath}")

            // Open the saved PDF
            val pdfUri = FileProvider.getUriForFile(this, "${packageName}.provider", pdfFile)
            val pdfIntent = Intent(Intent.ACTION_VIEW)
            pdfIntent.setDataAndType(pdfUri, "application/pdf")
            pdfIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(pdfIntent)

        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Error saving PDF")
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}