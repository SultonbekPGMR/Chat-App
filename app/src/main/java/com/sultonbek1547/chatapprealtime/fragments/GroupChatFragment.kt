package com.sultonbek1547.chatapprealtime.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sultonbek1547.chatapprealtime.adapters.GroupChatAdapter
import com.sultonbek1547.chatapprealtime.databinding.FragmentGroupChatBinding
import com.sultonbek1547.chatapprealtime.models.Group
import com.sultonbek1547.chatapprealtime.models.MessageText
import com.sultonbek1547.chatapprealtime.utils.MyData
import com.sultonbek1547.chatapprealtime.utils.MyData.GROUP
import com.sultonbek1547.chatapprealtime.utils.MyData.USER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class GroupChatFragment : Fragment() {


    private lateinit var binding: FragmentGroupChatBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var group: Group
    private lateinit var groupChatAdapter: GroupChatAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGroupChatBinding.inflate(layoutInflater, container, false)
        binding.btnSend.visibility = View.GONE
        binding.edtMessage.isActivated = true
        binding.edtMessage.isPressed = true
        binding.edtMessage.requestFocus()
        init()

        Handler().postDelayed({
            if (binding.progressBar.visibility == View.VISIBLE) {
                binding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
            }
        }, 3000)


        binding.btnSend.setOnClickListener {
            val edtMessage = binding.edtMessage.text.toString().trim()
            if (edtMessage.isNotEmpty()) {
                sendMessage(edtMessage)
            }
        }

        return binding.root
    }

    private fun init() {
        group = arguments?.getSerializable("group") as Group
        GROUP = group
        binding.tvGroupName.text = group.name
        binding.tvGroupMemberCount.text = group.groupMemberCount + " members"
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
            findNavController().popBackStack()
        }

        binding.edtMessage.addTextChangedListener {
            if (it != null) {
                if (it.isEmpty()) {
                    binding.btnSend.visibility = View.GONE
                } else {
                    binding.btnSend.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun getMessageList() {

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(Group::class.java)
                val gson = Gson()
                val listType = object : TypeToken<ArrayList<MessageText>>() {}.type
                val list: ArrayList<MessageText> = gson.fromJson(group?.listMessages, listType)
                groupChatAdapter.messageTextList = list
                groupChatAdapter.notifyDataSetChanged()
                if (list.size > 0) {
                    binding.myRv.scrollToPosition(list.size - 1)

                    if (binding.progressBar.visibility == View.VISIBLE) {
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                }

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
            val messageText = MessageText()
            val key = SimpleDateFormat("dd MM yyyy  HH mm ss SSS").format(Date())
            messageText.id = key
            messageText.date = SimpleDateFormat("HH:mm").format(Date())
            messageText.message = edtMessage
            messageText.senderId = USER.uid
            messageText.receiverId = USER.uid
            messageText.statusRead = "false"
            messageText.positon = groupChatAdapter.itemCount.toString()
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<MessageText>>() {}.type
            val messageTextList: ArrayList<MessageText> = gson.fromJson(group.listMessages, listType)
            messageTextList.add(messageText)
            group.listMessages = gson.toJson(messageTextList)
            reference.setValue(group)

            withContext(Dispatchers.Main) {
                binding.edtMessage.text.clear()
                binding.btnSend.isEnabled = true

            }
        }
    }


}