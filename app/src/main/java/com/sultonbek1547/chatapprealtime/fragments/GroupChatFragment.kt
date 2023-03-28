package com.sultonbek1547.chatapprealtime.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.adapters.GroupChatAdapter
import com.sultonbek1547.chatapprealtime.databinding.FragmentGroupChatBinding
import com.sultonbek1547.chatapprealtime.models.Group
import com.sultonbek1547.chatapprealtime.models.Message
import com.sultonbek1547.chatapprealtime.utils.MyData
import com.sultonbek1547.chatapprealtime.utils.MyData.USER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class GroupChatFragment : Fragment() {


    private val binding by lazy { FragmentGroupChatBinding.inflate(layoutInflater) }
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var group: Group
    private lateinit var groupChatAdapter: GroupChatAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        init()


        binding.btnSend.setOnClickListener {
            sendMessage(binding.edtMessage.text.toString())
        }

        return binding.root
    }

    private fun init() {
        group = arguments?.getSerializable("group") as Group
        binding.tvUserName.text = group.name
        //binding.tvUserStatusTime.text = "last seen at " + user.statusTime
        //Glide.with(binding.toolbar.context).load(user.imageLink).into(binding.userImage)

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("groups").child(group.id!!)
        groupChatAdapter = GroupChatAdapter(ArrayList(), MyData.USER.uid!!)
        binding.myRv.adapter = groupChatAdapter

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // The reference exists
                getMessageList()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })


        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(
                R.id.homeFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(findNavController().currentDestination?.id ?: 0, true).build()
            )
        }

    }

    private fun getMessageList() {

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(Group::class.java)

                val gson = Gson()
                val listType = object : TypeToken<ArrayList<Message>>() {}.type
                val list: ArrayList<Message> = gson.fromJson(group?.listMessages, listType)
                groupChatAdapter.messageList = list
                groupChatAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun sendMessage(edtMessage: String) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.btnSend.isEnabled = false
            }
            val message = Message()
            val key = SimpleDateFormat("dd MM yyyy  HH mm ss SSS").format(Date())
            message.id = key
            message.date = SimpleDateFormat("HH:mm").format(Date())
            message.message = edtMessage
            message.senderId = USER.uid
            message.receiverId = USER.uid
            message.statusRead = "false"
            message.positon = groupChatAdapter.itemCount.toString()
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Message>>() {}.type
            val messageList: ArrayList<Message> = gson.fromJson(group.listMessages, listType)
            messageList.add(message)
            group.listMessages = gson.toJson(messageList)
            reference.setValue(group)

            withContext(Dispatchers.Main) {
                binding.edtMessage.text.clear()
                binding.btnSend.isEnabled = true

            }
        }


    }

}