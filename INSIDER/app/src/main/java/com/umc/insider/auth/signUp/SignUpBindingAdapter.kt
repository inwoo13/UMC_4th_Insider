package com.umc.insider.auth.signUp

import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.umc.insider.R

@BindingAdapter("stateCheck")
fun StateCheck(view: EditText, editState: EditState) {
    when (editState) {
        EditState.CHECK -> {
            view.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                AppCompatResources.getDrawable(view.context, R.drawable.baseline_check_24), null
            )
        }

        EditState.CLOSE -> {
            view.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                AppCompatResources.getDrawable(view.context, R.drawable.baseline_close_24), null
            )
        }

        EditState.EMPTY -> {
            view.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                null, null
            )
        }
    }
}

@BindingAdapter("securityState")
fun SecurityState(view: EditText, securityState: SecurityState) {

    when (securityState) {
        SecurityState.SAFE -> {
            view.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                AppCompatResources.getDrawable(view.context, R.drawable.safe), null
            )
        }

        SecurityState.NORMAL -> {
            view.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                AppCompatResources.getDrawable(view.context, R.drawable.normal), null
            )
        }

        SecurityState.DANGER -> {
            view.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                AppCompatResources.getDrawable(view.context, R.drawable.danger), null
            )
        }

        SecurityState.EMPTY -> {
            view.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                null, null
            )
        }
    }
}

