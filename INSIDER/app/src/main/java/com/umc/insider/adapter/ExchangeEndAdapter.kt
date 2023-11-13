package com.umc.insider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.umc.insider.databinding.ExchangeEndListItemBinding
import com.umc.insider.databinding.ExchangingListItemBinding
import com.umc.insider.model.ExchangeItem
import com.umc.insider.model.Goods
import com.umc.insider.retrofit.model.ExchangesPostRes
import com.umc.insider.retrofit.model.GoodsGetRes

class ExchangeEndAdapter : ListAdapter<ExchangesPostRes, ExchangeEndAdapter.ExchangeEndViewHolder>(
    DiffCallback){

    companion object{
        private val DiffCallback = object  : DiffUtil.ItemCallback<ExchangesPostRes>(){
            override fun areItemsTheSame(oldItem: ExchangesPostRes, newItem: ExchangesPostRes): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ExchangesPostRes, newItem: ExchangesPostRes): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class ExchangeEndViewHolder(private val binding : ExchangingListItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(goods : ExchangesPostRes){
            binding.itemName.text = goods.name
            if(goods.weight.isNullOrBlank()){
                binding.itemWeightOrRest.text = "(${goods.count}개)"
            } else {
                binding.itemWeightOrRest.text = "(${goods.weight}g)"
            }

            Glide.with(binding.goodsImg.context)
                .load(goods.imageUrl)
                .placeholder(null)
                .transform(RoundedCorners(30))
                .into(binding.goodsImg)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeEndViewHolder {
        val binding = ExchangingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExchangeEndViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExchangeEndViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}