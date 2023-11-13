package com.umc.insider.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.umc.insider.OnNoteListener
import com.umc.insider.databinding.GoodsShortItemBinding
import com.umc.insider.retrofit.model.GoodsGetRes

class GoodsShortAdapter(private val listener : OnNoteListener) : ListAdapter<GoodsGetRes, GoodsShortAdapter.GoodsShortViewHolder>(DiffCallback) {

    companion object{
        private val DiffCallback = object  : DiffUtil.ItemCallback<GoodsGetRes>(){
            override fun areItemsTheSame(oldItem: GoodsGetRes, newItem: GoodsGetRes): Boolean {
                return oldItem.title == newItem.title
                //return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GoodsGetRes, newItem: GoodsGetRes): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class GoodsShortViewHolder(private val binding: GoodsShortItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goods : GoodsGetRes){
            binding.itemTitle.text = goods.title
            binding.itemName.text = goods.name
            binding.itemPrice.text = "${goods.price}원"
            if(goods.sale_price == null){
                binding.itemDiscountRateLayout.visibility = View.GONE
                binding.salePrice.visibility = View.GONE
                binding.itemDiscountRate.visibility = View.INVISIBLE
                binding.itemPrice.paintFlags = binding.itemPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.salePrice.text = ""
            }else{
                binding.itemDiscountRateLayout.visibility = View.VISIBLE
                binding.salePrice.visibility = View.VISIBLE
                binding.itemDiscountRate.visibility = View.VISIBLE
                binding.itemPrice.paintFlags = binding.itemPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.salePrice.text = "${goods.sale_price}원"
                binding.itemDiscountRate.text = "${goods.sale_percent}%"
            }


            if(goods.weight.isNullOrBlank()){
                binding.itemWeightOrRest.text = "(${goods.rest}개)"
                binding.itemUnit.text = "(개당)"
            }else{
                val weightText = if (goods.weight.toLong() >= 1000) {
                    String.format("(%.1fkg)", goods.weight.toLong() / 1000.0)
                } else {
                    "(${goods.weight}g)"
                }

                binding.itemWeightOrRest.text = weightText
                binding.itemUnit.text = "(100g당)"
            }


            Glide.with(binding.goodsImg.context)
                .load(goods.img_url)
                .placeholder(null)
                .transform(RoundedCorners(30))
                .into(binding.goodsImg)

            binding.goods.setOnClickListener {
                listener.onNotePurchaseDetail(goods.goods_id)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsShortViewHolder {
        val binding =
            GoodsShortItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoodsShortViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoodsShortViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}