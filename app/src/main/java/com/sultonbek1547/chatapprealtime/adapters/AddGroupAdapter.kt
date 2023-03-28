package com.sultonbek1547.chatapprealtime.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sultonbek1547.chatapprealtime.databinding.RvItemBinding
import com.sultonbek1547.chatapprealtime.models.User

class AddGroupAdapter(val list: ArrayList<User>, val function: (User, Boolean) -> Unit) :
    RecyclerView.Adapter<AddGroupAdapter.Vh>() {

    inner class Vh(private val itemRvBinding: RvItemBinding) :
        RecyclerView.ViewHolder(itemRvBinding.root) {
        fun onBind(user: User, position: Int) {
            itemRvBinding.myCheckBox.visibility = View.VISIBLE
            itemRvBinding.myCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                function(user,b)
            }

            itemRvBinding.tvName.text = user.name
            Glide.with(itemView.context).load(user.imageLink).into(itemRvBinding.image)

            if (user.isOnline == "true") {
                itemRvBinding.onlineImage.visibility = View.VISIBLE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
            holder.onBind(list[position], position)

    }


    override fun getItemCount(): Int = list.size


}