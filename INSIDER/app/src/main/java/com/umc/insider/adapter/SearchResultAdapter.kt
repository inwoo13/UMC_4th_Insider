package com.umc.insider.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.OnNoteListener
import com.umc.insider.databinding.SearchResultItemBinding
import com.umc.insider.model.SearchItem

class SearchResultAdapter(private val onNoteListener: OnNoteListener) :
    ListAdapter<SearchItem, SearchResultAdapter.SearchResultViewHolder>(DiffCallback){


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


    inner class SearchResultViewHolder(private val binding : SearchResultItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        init{
            itemView.setOnClickListener(this)
        }
        fun bind(searchItem: SearchItem){
            binding.itemName.text = searchItem.itemName
            binding.itemPrice.text = searchItem.itemPrice
            binding.itemWeightOrRest.text = "("+searchItem.itemWeight+")"
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                //onNoteListener.onNotePurchaseDetail(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = SearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItemAtPosition(position: Int): SearchItem {
        return getItem(position)
    }
}