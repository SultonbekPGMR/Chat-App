package com.sultonbek1547.chatapprealtime.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.adapters.GroupAdapter
import com.sultonbek1547.chatapprealtime.databinding.FragmentGroupsBinding
import com.sultonbek1547.chatapprealtime.models.Group
import com.sultonbek1547.chatapprealtime.utils.MyData.USER


class GroupsFragment : Fragment() {


    private val binding by lazy { FragmentGroupsBinding.inflate(layoutInflater) }
    private lateinit var databse: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var groupAdapter: GroupAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        databse = FirebaseDatabase.getInstance()
        reference = databse.getReference("groups")


        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<Group>()
                val children = snapshot.children
                children.forEach {
                    val group = it.getValue(Group::class.java)
                    if (group != null && group.groupUsersIds!!.contains(USER.uid!!)) list.add(group)

                }

                groupAdapter = GroupAdapter(list) { group: Group, position: Int ->
                    listItemClicked(
                        group,
                        position
                    )
                }
                binding.myRv.adapter = groupAdapter

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        return binding.root
    }

    private fun listItemClicked(group: Group, position: Int) {
        findNavController().navigate(R.id.groupChatFragment, bundleOf("group" to group))

    }


}