package com.sultonbek1547.chatapprealtime.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.adapters.ChatAdapter
import com.sultonbek1547.chatapprealtime.databinding.FragmentChatBinding
import com.sultonbek1547.chatapprealtime.models.Message
import com.sultonbek1547.chatapprealtime.models.User
import com.sultonbek1547.chatapprealtime.utils.MyData.USER
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class ChatFragment : Fragment() {
    private val binding by lazy { FragmentChatBinding.inflate(layoutInflater) }
    private lateinit var databse: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var user: User
    private lateinit var chatAdapter: ChatAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        init()

        binding.btnSend.setOnClickListener {
            val edtMessage = binding.edtMessage.text.toString().trim()
            if (edtMessage.isNotEmpty()) {
                sendMessage(edtMessage)
            }

        }

        return binding.root
    }

    private fun init() {
        user = arguments?.getSerializable("user") as User
        binding.tvUserName.text = user.name
        if (user.name == "Saved Messages") {
            binding.userImage.setImageResource(R.drawable.baseline_bookmark_border_24)
        } else {
            Glide.with(binding.toolbar.context).load(user.imageLink).into(binding.userImage)
            var padding = 20
            padding = padding.dpToPx(5)
            binding.userImage.setPadding(padding, padding, padding, padding)

        }

        databse = FirebaseDatabase.getInstance()
        reference = databse.getReference("chats").child(USER.uid + user.uid)
        chatAdapter = ChatAdapter(ArrayList(), USER.uid!!)
        binding.myRv.adapter = chatAdapter

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // The reference exists
                    getMessageList()
                } else {
                    // The reference does not exist
                    reference = databse.getReference("chats").child(user.uid + USER.uid)
                    getMessageList()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })


        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun getMessageList() {
        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Message::class.java)?.let { message ->
                    chatAdapter.messageList.add(message)
                    chatAdapter.notifyItemInserted(chatAdapter.itemCount - 1)
                    binding.myRv.scrollToPosition(chatAdapter.itemCount - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })


//
    }


    private fun sendMessage(edtMessage: String) {
        val message = Message()
        val key = SimpleDateFormat("dd MM yyyy  HH mm ss").format(Date())
        message.id = key
        message.date = SimpleDateFormat("HH:mm").format(Date())
        message.message = edtMessage
        message.senderId = USER.uid
        message.receiverId = user.uid
        reference.child(key)
            .setValue(message)

        binding.edtMessage.text.clear()
    }

    fun Int.dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).roundToInt()
    }

}