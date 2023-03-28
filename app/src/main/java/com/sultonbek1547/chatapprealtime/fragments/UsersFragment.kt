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
import com.sultonbek1547.chatapprealtime.utils.MyData.userList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
                userList = list

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


        return binding.root
    }


    private fun listItemClicked(user: User, position: Int) {

       findNavController().navigate(R.id.chatFragment, bundleOf("user" to user))
    }


}