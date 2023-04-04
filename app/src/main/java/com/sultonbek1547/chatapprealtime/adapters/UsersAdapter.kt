package com.sultonbek1547.chatapprealtime.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sultonbek1547.chatapprealtime.R
import com.sultonbek1547.chatapprealtime.databinding.RvItemBinding
import com.sultonbek1547.chatapprealtime.models.User

class UsersAdapter(private val  list: ArrayList<User>, val function: (User, Int) -> Unit) :
    RecyclerView.Adapter<UsersAdapter.Vh>() {

    inner class Vh(private val itemRvBinding: RvItemBinding) :
        RecyclerView.ViewHolder(itemRvBinding.root) {
        fun onBind(user: User, position: Int) {
            itemRvBinding.itemCard.setOnClickListener {
                function(user, position)
            }
            if (position == 0) {
                itemRvBinding.tvName.text = user.name
                itemRvBinding.image.setImageResource(R.drawable.baseline_bookmark_border_24)
                return
            }
            itemRvBinding.tvName.text = user.name
            Glide.with(itemView.context)
                .load(user.imageLink)
                .into(itemRvBinding.image)

            if (user.isOnline == "true") {
                itemRvBinding.onlineImage.visibility = View.VISIBLE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) =
        holder.onBind(list[position], position)


    override fun getItemCount(): Int = list.size


}