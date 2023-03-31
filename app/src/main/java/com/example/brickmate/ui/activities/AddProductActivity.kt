package com.example.brickmate.ui.activities

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.Toolbar
import com.example.brickmate.R

class AddProductActivity : BaseActivity(), OnClickListener {

    private var toolbarAddProductActivity : Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        initializeVars()
        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbarAddProductActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)
        }
        toolbarAddProductActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initializeVars() {
        toolbarAddProductActivity = findViewById(R.id.toolbar_add_product_activity)
    }

    override fun onClick(view: View?) {
        when(view?.id){
           R.id.iv_add_update_product -> {

           }
        }
    }
}