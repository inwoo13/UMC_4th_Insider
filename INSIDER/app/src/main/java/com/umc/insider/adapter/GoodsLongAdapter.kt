package com.umc.insider.adapter

import android.graphics.Paint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.umc.insider.OnNoteListener
import com.umc.insider.databinding.GoodsLongItemBinding
import com.umc.insider.retrofit.model.GoodsGetRes

class GoodsLongAdapter(private val listener : OnNoteListener) : ListAdapter<GoodsGetRes, GoodsLongAdapter.GoodsLongViewHolder>(DiffCallback) {

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

    inner class GoodsLongViewHolder(private val binding: GoodsLongItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(goods : GoodsGetRes){

                binding.itemTitle.text = goods.title
                binding.itemName.text = goods.name
                binding.itemPrice.text = "${goods.price}원"
                if(goods.sale_price == null){
                    binding.salePrice.visibility = View.INVISIBLE
                    binding.itemDiscountRate.visibility = View.INVISIBLE
                    binding.itemPrice.paintFlags = binding.itemPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    binding.salePrice.text = ""
                }else{
                    binding.salePrice.visibility = View.VISIBLE
                    binding.itemDiscountRate.visibility = View.VISIBLE
                    binding.itemPrice.paintFlags = binding.itemPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    binding.salePrice.text = "${goods.sale_price}원"
                    binding.itemDiscountRate.text = "${goods.sale_percent}%"
                }


                if(goods.weight.isNullOrBlank()){
                    binding.itemWeightOrRest.text = "(${goods.rest}개)"
                    binding.unit.text = "(개당)"
                }else{
                    val weightText = if (goods.weight.toLong() >= 1000) {
                        String.format("(%.1fkg)", goods.weight.toLong() / 1000.0)
                    } else {
                        "(${goods.weight}g)"
                    }

                    binding.itemWeightOrRest.text = weightText
                    binding.unit.text = "(100g당)"
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsLongViewHolder {
        val binding =
            GoodsLongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoodsLongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoodsLongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}