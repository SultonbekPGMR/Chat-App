package com.sultonbek1547.chatapprealtime.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sultonbek1547.chatapprealtime.fragments.GroupsFragment
import com.sultonbek1547.chatapprealtime.fragments.UsersFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = if (position == 0) {
            UsersFragment()
        } else {
            GroupsFragment()
        }
        return fragment
    }
}