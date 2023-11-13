package com.umc.insider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.databinding.CategoryImgItemBinding
import com.umc.insider.utils.CategoryClickListener

class CategoryImgAdapter(
    private val imageArray: IntArray,
    private val clickedImageArray: IntArray,
    private val firstPosition : Int,
    private val listener : CategoryClickListener
) : RecyclerView.Adapter<CategoryImgAdapter.CategoryImgViewHolder>() {

    private var selectedPosition = firstPosition

    inner class CategoryImgViewHolder(private val binding : CategoryImgItemBinding) : RecyclerView.ViewHolder(binding.root){


        fun bind(position: Int){

            if(selectedPosition == position){
                binding.categoryImage.setImageResource(clickedImageArray[position])
                listener.onImageTouch(position)
            }else{
                binding.categoryImage.setImageResource(imageArray[position])
            }

            binding.categoryImage.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryImgViewHolder {
        val binding = CategoryImgItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryImgViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageArray.size
    }

    override fun onBindViewHolder(holder: CategoryImgViewHolder, position: Int) {
        holder.bind(position)
    }
}