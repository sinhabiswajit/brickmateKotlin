package com.example.brickmate.ui.activities

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.brickmate.databinding.ActivityOrderListBinding
import com.example.brickmate.ui.adapters.OrderListFragmentAdapter
import com.example.brickmate.ui.fragments.NewOrdersFragment
import com.example.brickmate.ui.fragments.AllOrdersFragment
import com.example.brickmate.ui.fragments.PaidOrderFragment
import com.google.android.material.tabs.TabLayout

class OrderListActivity : BaseActivity(){
    private lateinit var binding: ActivityOrderListBinding
    private lateinit var adapter : OrderListFragmentAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        adapter = OrderListFragmentAdapter(supportFragmentManager, lifecycle)

        binding.tabLayoutOrderlist.addTab(binding.tabLayoutOrderlist.newTab().setText("Orders List"))
        binding.tabLayoutOrderlist.addTab(binding.tabLayoutOrderlist.newTab().setText("Paid Orders"))
        binding.tabLayoutOrderlist.addTab(binding.tabLayoutOrderlist.newTab().setText("All Orders"))

        binding.viewPagerOrderList.adapter = adapter

        binding.tabLayoutOrderlist.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPagerOrderList.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        binding.viewPagerOrderList.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayoutOrderlist.selectTab(binding.tabLayoutOrderlist.getTabAt(position))
            }
        })
    }


    private fun setUpActionBar() {
        toolbar = binding.toolbarOrderListActivity
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.icVectorBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        searchView = binding.searchViewOrderList
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val currentFragment = adapter.getFragment(binding.viewPagerOrderList.currentItem)
                if (currentFragment is NewOrdersFragment){
                    currentFragment.searchOrders(newText)
                }else if (currentFragment is PaidOrderFragment){
                    currentFragment.searchOrders(newText)
                }else if (currentFragment is AllOrdersFragment){
                    currentFragment.searchOrders(newText)
                }
                return true
            }
        })
    }

}