package com.example.realtimedatabasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realtimedatabasi.databinding.ItemUserBinding
import com.example.realtimedatabasi.models.User
import com.squareup.picasso.Picasso

class UsersAdapter(
    val list: List<User>,
    val onItemClickListener:(user:User)->Unit
):RecyclerView.Adapter<UsersAdapter.Vh>() {

    inner class Vh(val itemUserBinding: ItemUserBinding)
        :RecyclerView.ViewHolder(itemUserBinding.root){

            fun onBind(user: User){
                itemUserBinding.apply {
                    Picasso.get().load(user.photoUrl).into(img)
                     nameTv.text=user.displayName

                    itemView.setOnClickListener { onItemClickListener.invoke(user) }
                }
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount()=list.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])

    }
}