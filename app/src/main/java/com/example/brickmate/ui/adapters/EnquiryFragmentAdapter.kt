package com.example.brickmate.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.brickmate.ui.fragments.EnquiryFormFragment
import com.example.brickmate.ui.fragments.EnquiryListFragment

class EnquiryFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            EnquiryFormFragment()
            else
            EnquiryListFragment()

    }
}