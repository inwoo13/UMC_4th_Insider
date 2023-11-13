package com.umc.insider.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("sale")
fun View.discountOption(sale : Int?){

    visibility = if(sale == null) View.INVISIBLE else View.VISIBLE
}