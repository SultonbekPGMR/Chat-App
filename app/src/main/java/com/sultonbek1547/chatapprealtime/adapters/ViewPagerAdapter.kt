package com.sultonbek1547.chatapprealtime.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sultonbek1547.chatapprealtime.fragments.GroupsFragment
import com.sultonbek1547.chatapprealtime.fragments.UsersFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2 // return the number of fragments in the adapter
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UsersFragment() // return the first fragment for position 0
            1 -> GroupsFragment() // return the second fragment for position 1
            else -> throw IllegalArgumentException("Invalid position $position") // throw an exception for invalid positions
        }
    }
}