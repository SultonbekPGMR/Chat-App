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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.adapters.UsersAdapter
import com.sultonbek1547.chatapprealtime.databinding.FragmentUsersBinding
import com.sultonbek1547.chatapprealtime.models.User
import com.sultonbek1547.chatapprealtime.utils.MyData.USER


class UsersFragment : Fragment() {

    private val binding by lazy { FragmentUsersBinding.inflate(layoutInflater) }
    private lateinit var databse: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var usersAdapter: UsersAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        databse = FirebaseDatabase.getInstance()
        reference = databse.getReference("users")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<User>()
                val children = snapshot.children
                val owner = snapshot.child(USER.uid!!).getValue(User::class.java)
                owner?.imageLink = R.drawable.baseline_bookmark_border_24.toString()
                owner?.name = "Saved Messages"
                list.add(owner!!)
                children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null && user.uid != owner.uid) list.add(user)

                }
                usersAdapter = UsersAdapter(list) { user: User, position: Int ->
                    listItemClicked(
                        user,
                        position
                    )
                }
                binding.myRv.adapter = usersAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


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
                R.id.menu_settings -> {
                    Toast.makeText(context, "not available", Toast.LENGTH_SHORT).show()

                }
                R.id.menu_saved_messages -> {
                    listItemClicked(usersAdapter.list[0],0)
                }

            }

            true
        }


        return binding.root
    }

    private fun listItemClicked(user: User, position: Int) {

        findNavController().navigate(R.id.chatFragment, bundleOf("user" to user))
    }
}