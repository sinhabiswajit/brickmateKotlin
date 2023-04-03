package com.example.brickmate.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.Product
import com.example.brickmate.ui.adapters.ProductAdapter
import java.util.ArrayList

class ProductActivity : BaseActivity() {
    private var toolBarProductActivity : Toolbar? = null
    private lateinit var rvProductList : RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        initializeVars()
        setUpActionBar()
        //getProductList()
    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getProductListFromFireStore(this@ProductActivity)
    }

    private fun initializeVars() {
        toolBarProductActivity = findViewById(R.id.toolbar_product_activity)
        rvProductList = findViewById(R.id.rv_product_list)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.product_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_add_product -> {
                val intent = Intent(this@ProductActivity, AddProductActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolBarProductActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolBarProductActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun successGetProductListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        if (productsList.size > 0){
            rvProductList.layoutManager = LinearLayoutManager(this)
            rvProductList.setHasFixedSize(true)
            val adapterProducts = ProductAdapter(this@ProductActivity, productsList)
            rvProductList.adapter = adapterProducts
        }
    }
}