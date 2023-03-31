package com.example.brickmate.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.brickmate.R

class ProductActivity : AppCompatActivity() {
    private var toolBarProductActivity : Toolbar? = null
    private lateinit var rvProductList : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        initializeVars()
        setUpActionBar()
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
}