package com.sultonbek1547.chatapprealtime.fragments

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.adapters.ChatAdapter
import com.sultonbek1547.chatapprealtime.databinding.FragmentChatBinding
import com.sultonbek1547.chatapprealtime.models.Message
import com.sultonbek1547.chatapprealtime.models.MessageImage
import com.sultonbek1547.chatapprealtime.models.MessageText
import com.sultonbek1547.chatapprealtime.models.User
import com.sultonbek1547.chatapprealtime.utils.MyData.TYPE_IMAGE
import com.sultonbek1547.chatapprealtime.utils.MyData.TYPE_TEXT
import com.sultonbek1547.chatapprealtime.utils.MyData.USER
import com.sultonbek1547.chatapprealtime.utils.MyData.chatReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var databse: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var imageReference: StorageReference
    private lateinit var user: User
    private lateinit var chatAdapter: ChatAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        binding.btnSend.visibility = View.GONE
        binding.tvUserStatusTime.visibility = View.INVISIBLE
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


        // statusTime
        FirebaseDatabase.getInstance().getReference("users").child(user.uid!!)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (USER.uid === user.uid) {
                            return
                        }
                        binding.tvUserStatusTime.visibility = View.VISIBLE
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

        binding.btnImg.setOnClickListener {
            getImageFromDevice.launch("image/*")
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
            binding.tvUserStatusTime.text = "last seen at " + user.statusTime
        }

        databse = FirebaseDatabase.getInstance()
        reference = databse.getReference("chats").child(USER.uid + user.uid)
        imageReference = FirebaseStorage.getInstance().getReference("photos")
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
        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                chatAdapter.messages.add(
                    snapshot.getValue(Message::class.java)!!
                )
                chatAdapter.notifyItemInserted(chatAdapter.itemCount - 1)
                binding.myRv.scrollToPosition(chatAdapter.itemCount - 1)
                if (binding.progressBar.visibility == View.VISIBLE) {
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Message::class.java)?.let { message ->
                    chatAdapter.messages[message.positon!!.toInt()] = message
                    chatAdapter.notifyItemChanged(message.positon!!.toInt())
                    chatAdapter.messages[message.positon!!.toInt()] = message
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
            val messageText = MessageText()
            val key = SimpleDateFormat("dd MM yyyy  HH mm ss SSS").format(Date())
            messageText.id = key
            messageText.date = SimpleDateFormat("HH:mm").format(Date())
            messageText.message = edtMessage
            messageText.senderId = USER.uid
            messageText.receiverId = user.uid
            messageText.statusRead = if (USER.uid === user.uid) "true" else "false"
            messageText.positon = chatAdapter.itemCount.toString()

            val message = Message()
            message.textOrImage = TYPE_TEXT
            message.id = messageText.id
            message.messageText = messageText
            message.senderId = USER.uid
            message.receiverId = user.uid
            message.statusRead = if (USER.uid === user.uid) "true" else "false"
            message.positon = chatAdapter.itemCount.toString()
            message.date = SimpleDateFormat("HH:mm").format(Date())


            reference.child(key).setValue(message)

            withContext(Main) {
                binding.edtMessage.text.clear()
                binding.btnSend.isEnabled = true
            }
        }


    }

    private val getImageFromDevice =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                sendImage(it)
            }
        }

    private fun sendImage(imageUri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val uniqueId = UUID.randomUUID().toString()
            val task = imageReference.child(uniqueId).putFile(imageUri)
            task.addOnSuccessListener { taskSnapshot ->
                binding.progressBar.visibility = View.GONE
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                    // link of image is ready
                    val messageImage = MessageImage()
                    messageImage.id = uniqueId
                    messageImage.imageLink = it.toString()

                    val message = Message()
                    message.date = SimpleDateFormat("dd MM yyyy  HH mm ss SSS").format(Date())
                    message.id = message.date
                    message.senderId = USER.uid
                    message.receiverId = user.uid
                    message.statusRead = if (USER.uid === user.uid) "true" else "false"
                    message.positon = chatAdapter.itemCount.toString()
                    message.textOrImage = TYPE_IMAGE
                    message.messageImage = messageImage
                    message.messageText = null
                    reference.child(message.id!!).setValue(message)

                }

            }

            withContext(Dispatchers.Main) {
                task.addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()

                }
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