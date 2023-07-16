package com.umc.insider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.databinding.RankingItemBinding
import com.umc.insider.model.RankingItem
import com.umc.insider.utils.SearchesItemClickListener

class SearchTermsRankingAdapter(private val listener : SearchesItemClickListener) : ListAdapter<RankingItem, SearchTermsRankingAdapter.SearchTermsRankingViewHolder>(DiffCallback) {

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<RankingItem>(){
            override fun areItemsTheSame(oldItem: RankingItem, newItem: RankingItem): Boolean {
                return oldItem.ranking == newItem.ranking
            }

            override fun areContentsTheSame(oldItem: RankingItem, newItem: RankingItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class SearchTermsRankingViewHolder(private val binding : RankingItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(searchTerm : RankingItem){
            binding.rankingTextView.text = "${searchTerm.ranking}. "
            binding.searchTermTextview.text = "${searchTerm.searchTerm}"

            binding.root.setOnClickListener {
                listener.onClickSearch(searchTerm.searchTerm)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchTermsRankingViewHolder {
        val binding = RankingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchTermsRankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchTermsRankingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}