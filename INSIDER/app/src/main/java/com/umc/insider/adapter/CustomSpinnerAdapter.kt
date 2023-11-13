package com.umc.insider.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.umc.insider.R

class CustomSpinnerAdapter(private val context: Context, private val list: List<String>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var text: String = ""
    private var showArrowForSelectedItem: Boolean = false

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Set the TextView when the Spinner is displayed
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_outer_view, parent, false)
        }
        text = list[position]
        view?.findViewById<TextView>(R.id.spinner_inner_text)?.text = text
        return view!!
    }

    fun setSelectedItemVisibility(show: Boolean) {
        showArrowForSelectedItem = show
        notifyDataSetChanged()
    }

    // Set the TextView when the Spinner is clicked and the dropdown is shown
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_spinner, parent, false)
        }
        text = list[position]
        convertView!!.findViewById<TextView>(R.id.tvItemSpinner).text = text

        val arrowUpImageView = convertView.findViewById<ImageView>(R.id.arrowUpImageView)
        arrowUpImageView.visibility = if (position == 0 && showArrowForSelectedItem) View.VISIBLE else View.GONE

        val backgroundColor = ContextCompat.getColor(context, R.color.white)
        convertView.setBackgroundColor(backgroundColor)

        val drawable = ContextCompat.getDrawable(context, R.drawable.spinner_item_background)
        convertView.background = drawable

        // 모서리를 둥글게 설정
        when (position) {
            0 -> {
                convertView.background = ContextCompat.getDrawable(context, R.drawable.top_round)

            }
            6 -> convertView.background = ContextCompat.getDrawable(context, R.drawable.bottom_round)
            else -> convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

        return convertView
    }

    // Get the selected item from the Spinner in the activity
    fun getItem(): String {
        return text
    }
}