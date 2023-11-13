package com.umc.insider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.umc.insider.OnNoteListener
import com.umc.insider.databinding.ShoppingSaleListItemBinding
import com.umc.insider.model.SearchItem
import com.umc.insider.retrofit.model.GoodsGetRes

class ShoppingSaleAdapter: ListAdapter<GoodsGetRes, ShoppingSaleAdapter.ShoppingSaleViewHolder>(
    DiffCallback){

    companion object{
        private val DiffCallback = object  : DiffUtil.ItemCallback<GoodsGetRes>(){
            override fun areItemsTheSame(oldItem: GoodsGetRes, newItem: GoodsGetRes): Boolean {
                return oldItem.goods_id == newItem.goods_id
            }

            override fun areContentsTheSame(oldItem: GoodsGetRes, newItem: GoodsGetRes): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class ShoppingSaleViewHolder(private val binding : ShoppingSaleListItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(goods : GoodsGetRes){
            binding.itemName.text = goods.name
            binding.itemPrice.text = "${goods.price}원"

            if(goods.weight == "String"){
                binding.itemWeightOrRest.text = "20g"
                binding.unit.text = "(100g당)"
            } else {
                if(goods.weight.isNullOrBlank()){
                    binding.itemWeightOrRest.text = "(${goods.rest}개)"
                    binding.unit.text = "(개당)"
                } else {
                    val weightText = if (goods.weight.toLong() >= 1000) {
                        String.format("(%.1fkg)", goods.weight.toLong() / 1000.0)
                    } else {
                        "(${goods.weight}g)"
                    }

                    binding.itemWeightOrRest.text = weightText
                    binding.unit.text = "(100g당)"
                }
            }

            Glide.with(binding.goodsImg.context)
                .load(goods.img_url)
                .placeholder(null)
                .transform(RoundedCorners(30))
                .into(binding.goodsImg)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingSaleViewHolder {
        val binding =
            ShoppingSaleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingSaleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingSaleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}