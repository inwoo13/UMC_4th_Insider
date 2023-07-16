package com.umc.insider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.databinding.RecentSearchItemBinding
import com.umc.insider.model.RecentSearchItem
import com.umc.insider.utils.SearchesItemClickListener

class RecentSearchesAdapter(private val listener : SearchesItemClickListener) : ListAdapter<RecentSearchItem, RecentSearchesAdapter.RecentSearchesViewHolder>(DiffCallback){

    companion object{
        private val DiffCallback = object  : DiffUtil.ItemCallback<RecentSearchItem>(){
            override fun areItemsTheSame(oldItem: RecentSearchItem, newItem: RecentSearchItem): Boolean {
                return oldItem.searchKeyword == newItem.searchKeyword
            }

            override fun areContentsTheSame(oldItem: RecentSearchItem, newItem: RecentSearchItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class RecentSearchesViewHolder(private val binding : RecentSearchItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(recentSearchTerm : RecentSearchItem){
            binding.recentSearchItem.text = recentSearchTerm.searchKeyword

            binding.recentSearchItem.setOnClickListener {
                listener.onClickSearch(recentSearchTerm.searchKeyword)
            }

            binding.deleteBtn.setOnClickListener {
                listener.onClickDelete(recentSearchTerm.searchKeyword)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchesViewHolder {
        val binding = RecentSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentSearchesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentSearchesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}