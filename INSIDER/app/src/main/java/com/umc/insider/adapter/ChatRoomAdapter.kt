package com.umc.insider.adapter

import android.content.Context
import android.text.StaticLayout
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.umc.insider.R
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.ChatRoomItemBinding
import com.umc.insider.model.ChatRoomItem
import com.umc.insider.retrofit.model.MessageGetRes

class ChatRoomAdapter(context : Context) : ListAdapter<MessageGetRes,ChatRoomAdapter.ChatRoomViewHolder>(DiffCallback)  {

    private val my_id = UserManager.getUserIdx(context)!!.toLong()
    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<MessageGetRes>(){
            override fun areItemsTheSame(oldItem: MessageGetRes, newItem: MessageGetRes): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MessageGetRes, newItem: MessageGetRes): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class ChatRoomViewHolder(private val binding : ChatRoomItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(messageGetRes: MessageGetRes) {

            binding.message.text = messageGetRes.content

            if (messageGetRes.senderId.id == my_id) {
                binding.chatLayout.gravity = Gravity.END
                binding.backgroundImage.setImageResource(R.drawable.my_chat)
            } else {
                binding.chatLayout.gravity = Gravity.START
                binding.backgroundImage.setImageResource(R.drawable.opponent_chat)
            }

            binding.message.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    binding.message.viewTreeObserver.removeOnPreDrawListener(this)

                    val padding = binding.message.context.dpToPx(5f)
                    val maxWidth = binding.message.context.dpToPx(200f)

                    val staticLayout = StaticLayout.Builder.obtain(
                        messageGetRes.content, 0, messageGetRes.content.length, binding.message.paint, maxWidth
                    ).build()

                    var maxLineWidth = 0f
                    for (i in 0 until staticLayout.lineCount) {
                        val lineWidth = staticLayout.getLineWidth(i)
                        if (lineWidth > maxLineWidth) {
                            maxLineWidth = lineWidth
                        }
                    }

                    val textHeight = staticLayout.height

                    // Considering line spacing
                    val lineSpacingExtra = binding.message.lineSpacingExtra+10
                    val lineSpacingMultiplier = binding.message.lineSpacingMultiplier

                    // Adjusting the height considering both the line spacing extra and multiplier
                    val textHeightWithSpacing = (textHeight + (staticLayout.lineCount - 1) * lineSpacingExtra) * lineSpacingMultiplier

                    val calculatedWidth = (maxLineWidth + 2 * padding).coerceAtMost(maxWidth.toFloat()).toInt()
                    val calculatedHeight = (textHeightWithSpacing + 2 * padding).toInt()

                    val layoutParamsTextView = binding.message.layoutParams
                    layoutParamsTextView.width = calculatedWidth
                    layoutParamsTextView.height = calculatedHeight
                    binding.message.layoutParams = layoutParamsTextView

                    val layoutParamsImageView = binding.backgroundImage.layoutParams
                    layoutParamsImageView.width = calculatedWidth
                    layoutParamsImageView.height = calculatedHeight
                    binding.backgroundImage.layoutParams = layoutParamsImageView

                    return true
                }
            })


        }
    }

    fun Context.dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics
        ).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val binding = ChatRoomItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}