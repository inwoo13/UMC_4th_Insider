package com.umc.insider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.databinding.DiscountShortItemBinding
import com.umc.insider.model.SearchItem

class DiscountGoodsAdapter : ListAdapter<SearchItem, DiscountGoodsAdapter.SaleGoodsViewHolder>(DiffCallback){

    companion object{
        private val DiffCallback = object  : DiffUtil.ItemCallback<SearchItem>(){
            override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class SaleGoodsViewHolder(private val binding : DiscountShortItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(listItem : SearchItem){
            binding.itemName.text = listItem.itemName
            binding.itemPrice.text = listItem.itemPrice
            binding.itemWeightOrRest.text = listItem.itemWeight
            binding.salePrice.text = listItem.salePrice
            binding.discountRate.text = listItem.discountRate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleGoodsViewHolder {
        val binding = DiscountShortItemBinding.inflate(LayoutInflater.from(parent.context),parent, false )
        return SaleGoodsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleGoodsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}