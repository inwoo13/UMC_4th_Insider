package com.umc.insider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.databinding.ExchangingListItemBinding
import com.umc.insider.databinding.ShoppingSaleListItemBinding
import com.umc.insider.model.ExchangeItem
import com.umc.insider.model.SearchItem

class ExchangingListAdapter : ListAdapter<ExchangeItem, ExchangingListAdapter.ExchangingViewHolder>(
    DiffCallback) {

    companion object{
        private val DiffCallback = object  : DiffUtil.ItemCallback<ExchangeItem>(){
            override fun areItemsTheSame(oldItem: ExchangeItem, newItem: ExchangeItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ExchangeItem, newItem: ExchangeItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class ExchangingViewHolder(private val binding : ExchangingListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(listItem : ExchangeItem){
            binding.itemName.text = listItem.itemName
            //binding.itemAmount.text = "("+listItem.itemAmount+")"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangingListAdapter.ExchangingViewHolder {
        val binding = ExchangingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExchangingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExchangingListAdapter.ExchangingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}