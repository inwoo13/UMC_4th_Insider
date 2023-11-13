package com.umc.insider.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.R
import com.umc.insider.utils.CategoryClickListener

class CategoryAdapter(
    private val imageArray: IntArray,
    private val clickedImageArray: IntArray,
    private val listener: CategoryClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.categoryImg)
        private val gestureListener = GestureListener(itemView.context)

        init {
            imageView.setOnTouchListener { _, event -> gestureListener.onTouch(imageView, event) }
        }

        inner class GestureListener(context: Context) : GestureDetector.SimpleOnGestureListener() {
            private val gestureDetector = GestureDetector(context, this)

            fun onTouch(v: View, event: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(event)
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                imageView.setImageResource(clickedImageArray[adapterPosition])
                listener.onImageTouch(adapterPosition)
                return true
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                return true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.imageView.setImageResource(imageArray[position])
    }

    override fun getItemCount() = imageArray.size
}