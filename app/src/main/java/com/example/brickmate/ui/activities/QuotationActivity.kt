package com.example.brickmate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityEnquiryBinding
import com.example.brickmate.ui.adapters.QuotationFragmentAdapter
import com.google.android.material.tabs.TabLayout

class QuotationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnquiryBinding
    private lateinit var adapter: QuotationFragmentAdapter
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()

        adapter = QuotationFragmentAdapter(supportFragmentManager, lifecycle)

        binding.tabLayoutEnquiry.addTab(binding.tabLayoutEnquiry.newTab().setText("Quotation"))
        binding.tabLayoutEnquiry.addTab(binding.tabLayoutEnquiry.newTab().setText("Quote List"))

        binding.viewPagerEnquiry.adapter = adapter

        binding.tabLayoutEnquiry.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPagerEnquiry.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        binding.viewPagerEnquiry.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayoutEnquiry.selectTab(binding.tabLayoutEnquiry.getTabAt(position))
            }
        })
    }

    private fun setUpActionBar() {
        toolbar = binding.toolbarEnquiryActivity
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_light)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}