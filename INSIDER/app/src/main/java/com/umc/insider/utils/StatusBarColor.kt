package com.umc.insider.utils

import android.app.Activity
import android.os.Build
import android.view.Window
import android.view.WindowManager

fun Activity.changeStatusBarColor(color: Int) {
    val window: Window? = this.window
    window?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            it.statusBarColor = color
        }
    }
}