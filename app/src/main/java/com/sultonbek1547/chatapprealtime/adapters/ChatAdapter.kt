package com.sultonbek1547.chatapprealtime.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.databinding.ItemReceivedMessageBinding
import com.sultonbek1547.chatapprealtime.databinding.ItemSentMessageBinding
import com.sultonbek1547.chatapprealtime.models.Message

class ChatAdapter(val messageList: ArrayList<Message>, private val senderId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                val binding = ItemReceivedMessageBinding.inflate(layoutInflater, parent, false)
                ReceivedMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
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
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.senderId == senderId) {
            SENT_MESSAGE
        } else {
            RECEIVED_MESSAGE
        }
    }

    class SentMessageViewHolder(private val binding: ItemSentMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            // Bind the sent message data to the views in the sent message layout using view binding
            binding.tvMessage.text = message.message
            binding.tvMessageContent.text = message.date
        }
    }

    class ReceivedMessageViewHolder(private val binding: ItemReceivedMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            // Bind the received message data to the views in the received message layout using view binding
            binding.tvMessage.text = message.message
            binding.tvMessageContent.text = message.date
        }
    }
}