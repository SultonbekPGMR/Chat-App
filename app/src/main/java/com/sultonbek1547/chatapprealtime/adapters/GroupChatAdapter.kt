package com.sultonbek1547.chatapprealtime.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.databinding.ItemReceivedMessageGroupBinding
import com.sultonbek1547.chatapprealtime.databinding.ItemSentMessageBinding
import com.sultonbek1547.chatapprealtime.models.MessageText
import com.sultonbek1547.chatapprealtime.utils.MyData
import com.sultonbek1547.chatapprealtime.utils.MyData.GROUP
import com.sultonbek1547.chatapprealtime.utils.MyData.getSenderUser

class GroupChatAdapter(var messageTextList: ArrayList<MessageText>, private val senderId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENT_MESSAGE = 1
    private val RECEIVED_MESSAGE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SENT_MESSAGE -> {
                val binding = ItemSentMessageBinding.inflate(layoutInflater, parent, false)
                SentMessageViewHolder(binding)
            }
            RECEIVED_MESSAGE -> {
                val binding = ItemReceivedMessageGroupBinding.inflate(layoutInflater, parent, false)
                ReceivedMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageTextList[position]
        when (holder.itemViewType) {
            SENT_MESSAGE -> {
                val sentHolder = holder as SentMessageViewHolder
                sentHolder.bind(message)
            }
            RECEIVED_MESSAGE -> {
                val receivedHolder = holder as ReceivedMessageViewHolder
                receivedHolder.bind(message)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageTextList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageTextList[position]
        return if (message.senderId == senderId) {
            SENT_MESSAGE
        } else {
            RECEIVED_MESSAGE
        }
    }

    class SentMessageViewHolder(private val binding: ItemSentMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(messageText: MessageText) {
            // Bind the sent message data to the views in the sent message layout using view binding
            binding.tvMessage.text = messageText.message
            binding.tvMessageContent.text = messageText.date
            binding.tvMessage.maxWidth = MyData.screenLengthItem
            if (messageText.statusRead == "true") {
                binding.imgMessageStatus.setImageResource(R.drawable.double_checkmark)
            } else {
                binding.imgMessageStatus.setImageResource(R.drawable.single_checkmark)
            }
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemReceivedMessageGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(messageText: MessageText) {
            // Bind the received message data to the views in the received message layout using view binding
            binding.tvMessage.text = messageText.message
            binding.tvMessageContent.text = messageText.date
            val user = getSenderUser(messageText.senderId!!)
            Glide.with(itemView.context).load(user.imageLink).into(binding.image)
            binding.tvUserName.text = user.name
            if (user.isOnline == "true") {
                binding.onlineImage.visibility = View.VISIBLE
            }

            if (messageText.statusRead != "true") {
                messageText.statusRead = "true"
                val gson = Gson()
                messageTextList[position] = messageText
                GROUP.listMessages = gson.toJson(messageTextList)
                val database = FirebaseDatabase.getInstance()
                val reference: DatabaseReference = database.getReference("groups").child(GROUP.id!!)
                reference.setValue(GROUP)
            }

        }
    }


}