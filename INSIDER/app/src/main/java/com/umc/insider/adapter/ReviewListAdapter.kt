package com.umc.insider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.databinding.ReviewListItemBinding
import com.umc.insider.model.reviewListItem
import com.umc.insider.utils.ReviewListClickListener
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewListAdapter(
    private val reviewList: List<reviewListItem>,
    private val listener: ReviewListClickListener
) : RecyclerView.Adapter<ReviewListAdapter.ReviewListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListViewHolder {
        val binding = ReviewListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewListViewHolder, position: Int) {
        val currentReviewItem = reviewList[position]
        holder.bindData(currentReviewItem)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ReviewListViewHolder(private val binding: ReviewListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(reviewItem: reviewListItem) {
            binding.reviewTitle.text = reviewItem.reviewTitle
            binding.reviewScore.text = reviewItem.reviewScore
            binding.reviewDetail.text = reviewItem.reviewDetail

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(reviewItem.reviewDate)
            binding.reviewDate.text = formattedDate

            binding.root.setOnClickListener {
                listener.ReviewListItemClick() // 변경된 인터페이스 메서드 사용
            }
        }
    }
}
