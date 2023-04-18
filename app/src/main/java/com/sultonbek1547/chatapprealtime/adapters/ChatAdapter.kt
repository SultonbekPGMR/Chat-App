package com.sultonbek1547.chatapprealtime.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.databinding.ItemReceivedMessageBinding
import com.sultonbek1547.chatapprealtime.databinding.ItemSentMessageBinding
import com.sultonbek1547.chatapprealtime.models.Message
import com.sultonbek1547.chatapprealtime.utils.MyData.TYPE_TEXT
import com.sultonbek1547.chatapprealtime.utils.MyData.chatReference
import com.sultonbek1547.chatapprealtime.utils.MyData.screenLengthItem

class ChatAdapter(val messages: ArrayList<Message>, private val senderId: String) :
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
                val binding = ItemReceivedMessageBinding.inflate(layoutInflater, parent, false)
                ReceivedMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
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
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == senderId) {
            SENT_MESSAGE
        } else {
            RECEIVED_MESSAGE
        }
    }

    class SentMessageViewHolder(private val binding: ItemSentMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            // Bind the sent message data to the views in the sent message layout using view binding
            if (message.textOrImage == TYPE_TEXT) {
                binding.tvMessage.text = message.messageText!!.message
                binding.tvMessageContent.text = message.date
                binding.tvMessage.maxWidth = screenLengthItem
                if (message.statusRead == "true") {
                    binding.imgMessageStatus.setImageResource(R.drawable.double_checkmark)
                } else {
                    binding.imgMessageStatus.setImageResource(R.drawable.single_checkmark)
                }
            } else {
                binding.constraintLayout.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE
                Picasso.get().load(message.messageImage!!.imageLink).into(binding.imageView)
            }

        }
    }

    class ReceivedMessageViewHolder(private val binding: ItemReceivedMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            // Bind the received message data to the views in the received message layout using view binding
            if (message.textOrImage == TYPE_TEXT) {
                binding.tvMessage.text = message.messageText!!.message
                binding.tvMessageContent.text = message.date
                if (message.statusRead != "true") {
                    message.statusRead = "true"
                    chatReference!!.child(message.id!!).setValue(message)
                }
            } else {
                binding.imageView.visibility = View.VISIBLE
                Picasso.get().load(message.messageImage!!.imageLink).into(binding.imageView)

            }


        }
    }

}
