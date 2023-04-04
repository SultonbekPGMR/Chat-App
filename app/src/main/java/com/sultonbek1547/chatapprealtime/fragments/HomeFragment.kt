package com.sultonbek1547.chatapprealtime.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.adapters.ViewPagerAdapter
import com.sultonbek1547.chatapprealtime.databinding.FragmentHomeBinding
import com.sultonbek1547.chatapprealtime.utils.MyData.USER
import com.sultonbek1547.chatapprealtime.utils.MyData.userList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {


    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        init()

        return binding.root
    }

    private fun init() {
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        binding.myViewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.myTabLayout, binding.myViewPager) { tab, position ->

            when (position) {
                0 -> tab.text = "Personal"
                1 -> tab.text = "Groups"
            }
        }.attach()


        // head layout init
        val head = binding.myNavView.getHeaderView(0)
        head.rootView.findViewById<TextView>(R.id.tv_user_name).text = USER.name
        head.rootView.findViewById<TextView>(R.id.tv_user_email).text = USER.email
        Glide.with(head.rootView.context).load(USER.imageLink).into(
            head.rootView.findViewById<ImageView>(R.id.nav_header_icon)
        )

        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        binding.myNavView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_log_out -> {
                    USER.statusTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    USER.isOnline = "false"
                    FirebaseDatabase.getInstance().getReference("users")
                        .child(FirebaseAuth.getInstance().uid!!).setValue(USER)

                    FirebaseAuth.getInstance().signOut()
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build()

                    val googleSigningClient = GoogleSignIn.getClient(requireActivity(), gso)
                    googleSigningClient.signOut()

                    findNavController().navigate(
                        R.id.authFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(findNavController().currentDestination?.id ?: 0, true)
                            .build()
                    )

                }
                R.id.menu_about -> {
                    Toast.makeText(context, "Chat app: Sultonbek. 27.03.2023", Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.menu_add_group -> {

                    findNavController().navigate(R.id.addGroupFragment)

                }
                R.id.menu_settings -> {
                    Toast.makeText(context, "not available", Toast.LENGTH_SHORT).show()

                }
                R.id.menu_saved_messages -> {
                    findNavController().navigate(R.id.chatFragment, bundleOf("user" to userList[0]))
                }
            }

            true
        }

    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            USER.statusTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            USER.isOnline = "true"
            FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().uid!!).setValue(USER)
        }

    }
}