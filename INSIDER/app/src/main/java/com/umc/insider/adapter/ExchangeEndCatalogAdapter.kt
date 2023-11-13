package com.umc.insider.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.OnNoteListener
import com.umc.insider.databinding.ExchangeEndItemBinding
import com.umc.insider.model.ExchangeItem

class ExchangeEndCatalogAdapter(private val onNoteListener: OnNoteListener) :
    ListAdapter<ExchangeItem, ExchangeEndCatalogAdapter.ExchangeEndCatalogViewHolder>(DiffCallback){

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

    inner class ExchangeEndCatalogViewHolder(private val binding : ExchangeEndItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        init{
            itemView.setOnClickListener(this)
        }

        fun bind(listItem : ExchangeItem){
            binding.itemName.text = listItem.itemName
            binding.itemAmount.text = "(" + listItem.itemAmount + ")"
            binding.itemExchange.text = listItem.itemExchange
            binding.itemExchangeAmount.text = listItem.itemExchangeAmount
        }


        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                //onNoteListener.onNotePurchaseDetail(position)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeEndCatalogViewHolder {
        val binding = ExchangeEndItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExchangeEndCatalogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExchangeEndCatalogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItemAtPosition(position: Int): ExchangeItem {
        return getItem(position)
    }

}
