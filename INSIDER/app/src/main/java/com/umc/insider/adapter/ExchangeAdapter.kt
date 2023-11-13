package com.umc.insider.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umc.insider.OnNoteListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.umc.insider.databinding.ExchangeItemBinding
import com.umc.insider.model.ExchangeItem
import com.umc.insider.retrofit.model.ExchangesPostRes
import okhttp3.internal.connection.Exchange

class ExchangeAdapter(private val onNoteListener: OnNoteListener) :
    ListAdapter<ExchangesPostRes, ExchangeAdapter.ExchangeViewHolder>(DiffCallback){

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

    inner class ExchangeViewHolder(private val binding : ExchangeItemBinding) :
        RecyclerView.ViewHolder(binding.root){

            fun bind(exchangesPostRes : ExchangesPostRes){
                binding.itemTitle.text = exchangesPostRes.title
                binding.itemName.text = exchangesPostRes.name
                if (exchangesPostRes.weight.isBlank()){
                    binding.itemWeightOrRest.text = "${exchangesPostRes.count}개"
                }else{
                    val weightText = if (exchangesPostRes.weight.toLong() >= 1000) {
                        String.format("(%.1fkg)", exchangesPostRes.weight.toLong() / 1000.0)
                    } else {
                        "(${exchangesPostRes.weight}g)"
                    }
                    binding.itemWeightOrRest.text = "$weightText"
                }
                binding.exchangeText.text = "${exchangesPostRes.wantItem} (이랑)\n교환 원해요!"
                Glide.with(binding.goodsImg.context)
                    .load(exchangesPostRes.imageUrl)
                    .placeholder(null)
                    .into(binding.goodsImg)

                binding.goods.setOnClickListener {
                    onNoteListener.onNotePurchaseDetail(exchangesPostRes.id)
                }
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        val binding = ExchangeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExchangeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItemAtPosition(position: Int): ExchangesPostRes {
        return getItem(position)
    }

}