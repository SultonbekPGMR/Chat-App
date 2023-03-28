package com.sultonbek1547.chatapprealtime.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.adapters.ChatAdapter
import com.sultonbek1547.chatapprealtime.databinding.FragmentChatBinding
import com.sultonbek1547.chatapprealtime.models.Message
import com.sultonbek1547.chatapprealtime.models.User
import com.sultonbek1547.chatapprealtime.utils.MyData.USER
import com.sultonbek1547.chatapprealtime.utils.MyData.chatReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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


        // statusTime
        FirebaseDatabase.getInstance().getReference("users").child(user.uid!!)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                     if (USER.uid === user.uid) {
                         binding.tvUserStatusTime.text =  ""
                         return
                        }
                        if (snapshot.getValue(User::class.java)?.isOnline == "true") {
                            binding.tvUserStatusTime.text = "online"
                            binding.tvUserStatusTime.setTextColor(Color.parseColor("#008FF1"))
                        } else {
                            binding.tvUserStatusTime.text =
                                "last seen at " + snapshot.getValue(User::class.java)?.statusTime
                            binding.tvUserStatusTime.setTextColor(Color.parseColor("#80FFFFFF"))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                }
            )

        return binding.root
    }

    private fun init() {
        user = arguments?.getSerializable("user") as User
        binding.tvUserName.text = user.name
        binding.tvUserStatusTime.text = "last seen at " + user.statusTime

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
            findNavController().navigate(
                R.id.homeFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(findNavController().currentDestination?.id ?: 0, true).build()
            )
        }

    }

    private fun getMessageList() {
        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                chatAdapter.messageList.add(
                    snapshot.getValue(Message::class.java)!!
                )
                chatAdapter.notifyItemInserted(chatAdapter.itemCount - 1)
                binding.myRv.scrollToPosition(chatAdapter.itemCount - 1)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Message::class.java)?.let { message ->
                    chatAdapter.messageList[message.positon!!.toInt()] = message
                    chatAdapter.notifyItemChanged(message.positon!!.toInt())
                    chatAdapter.messageList[message.positon!!.toInt()] = message
                    chatAdapter.notifyItemChanged(message.positon!!.toInt())
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })

        chatReference = reference
    }


    private fun sendMessage(edtMessage: String) {
        CoroutineScope(IO).launch {
            withContext(Main) {
                binding.btnSend.isEnabled = false
            }
            val message = Message()
            val key = SimpleDateFormat("dd MM yyyy  HH mm ss SSS").format(Date())
            message.id = key
            message.date = SimpleDateFormat("HH:mm").format(Date())
            message.message = edtMessage
            message.senderId = USER.uid
            message.receiverId = user.uid
            message.statusRead = if (USER.uid === user.uid) "true" else "false"
            message.positon = chatAdapter.itemCount.toString()
            reference.child(key)
                .setValue(message)
            withContext(Main) {
                binding.edtMessage.text.clear()
                binding.btnSend.isEnabled = true

            }
        }


    }

    private fun Int.dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).roundToInt()
    }

    override fun onStop() {
        super.onStop()
        binding.myRv.adapter = null
    }
}