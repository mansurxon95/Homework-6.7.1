package com.example.a6711.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a6711.databinding.ItemRcBinding
import com.example.a6711.entitiy.PassportEntity

class RcAdapter(var onClik: OnClik): ListAdapter<PassportEntity, RcAdapter.VH>(MyDiffUtill()) {

    inner class VH(var itemview : ItemRcBinding): RecyclerView.ViewHolder(itemview.root){

        fun onBind(user: PassportEntity){

            itemview.name.text = "${user.lastName} ${user.firstName}"
            itemview.seriya.text = "AA ${user.passportId?.plus(10000000)}"
            itemview.btnMenu.setOnClickListener {
                onClik.click(user,itemview.btnMenu)
            }
            itemview.click.setOnClickListener {
                onClik.clickview(user)
            }
        }

    }

    class MyDiffUtill: DiffUtil.ItemCallback<PassportEntity>(){
        override fun areItemsTheSame(oldItem: PassportEntity, newItem: PassportEntity): Boolean {
            return  oldItem.passportId == newItem.passportId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: PassportEntity, newItem: PassportEntity): Boolean {
            return  oldItem==newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRcBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }

    interface OnClik{
        fun click(contact: PassportEntity,btn: View){

        }
        fun clickview(contact: PassportEntity){

        }
    }


}