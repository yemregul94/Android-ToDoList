package com.moonlight.todolist.util

import android.app.Activity
import android.app.AlertDialog
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE
import com.moonlight.todolist.R

class CustomAlertDialog {

    interface CustomAlertListener {
        fun onPositiveButtonClick(settingTextFirst: String, settingTextSecond: String, check: Boolean)
    }

    fun showCustomAlert(activity: Activity, inputType: String?, inputString: String?, inputCheck: Boolean?, listener: CustomAlertListener?) {
        val alertlayout = LayoutInflater.from(activity.baseContext).inflate(R.layout.alert_double_text, null, false)
        val itemTextFirst = alertlayout.findViewById<TextInputEditText>(R.id.txt_item_first)
        val itemTextSecond = alertlayout.findViewById<TextInputEditText>(R.id.txt_item_second)
        val layoutFirst = alertlayout.findViewById<TextInputLayout>(R.id.layout_item_first)
        val layoutSecond = alertlayout.findViewById<TextInputLayout>(R.id.layout_item_second)
        val itemCheck = alertlayout.findViewById<CheckBox>(R.id.item_check)
        val positiveText: String

        val builder = AlertDialog.Builder(activity)

        when(inputType){
            "category" -> {
                layoutSecond.visibility = View.GONE
                itemCheck.visibility = View.GONE
                itemTextFirst.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or
                        InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT

                if(inputString == null){
                    builder.setTitle(activity.baseContext.getString(R.string.add_new_category))
                    layoutFirst.hint = activity.baseContext.getString(R.string.input_category_name)
                    positiveText = activity.baseContext.getString(R.string.add)
                }
                else{
                    builder.setTitle(activity.baseContext.getString(R.string.edit_category))
                    itemTextFirst.setText(inputString)
                    layoutFirst.hint = activity.baseContext.getString(R.string.input_category_name)
                    positiveText = activity.baseContext.getString(R.string.update)
                }
            }
            "subTask" -> {
                layoutSecond.visibility = View.GONE
                itemTextFirst.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or
                        InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT

                if(inputString == null || inputCheck == null){
                    builder.setTitle(activity.baseContext.getString(R.string.add_new_sub_task))
                    layoutFirst.hint = activity.baseContext.getString(R.string.input_sub_task_name)
                    positiveText = activity.baseContext.getString(R.string.add)
                }
                else{
                    builder.setTitle(activity.baseContext.getString(R.string.edit_sub_task))
                    itemTextFirst.setText(inputString)
                    layoutFirst.hint = activity.baseContext.getString(R.string.input_sub_task_name)
                    positiveText = activity.baseContext.getString(R.string.update)
                    itemCheck.isChecked = inputCheck
                }
            }
            "anon" -> {
                itemCheck.visibility = View.GONE
                builder.setTitle(activity.baseContext.getString(R.string.save_guest_account))
                positiveText = activity.baseContext.getString(R.string.save)

                layoutFirst.hint = activity.baseContext.getString(R.string.input_email)
                itemTextFirst.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or
                        InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT

                layoutSecond.hint = activity.baseContext.getString(R.string.input_new_password)
                itemTextSecond.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                layoutSecond.endIconMode = END_ICON_PASSWORD_TOGGLE
                layoutSecond.setEndIconTintList(ContextCompat.getColorStateList(activity, R.color.theme_primary))
            }
            "email" -> {
                itemCheck.visibility = View.GONE
                builder.setTitle(activity.baseContext.getString(R.string.change_email_address))
                positiveText = activity.baseContext.getString(R.string.update)

                layoutFirst.hint = activity.baseContext.getString(R.string.input_new_email)
                itemTextFirst.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or
                        InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                itemTextFirst.setText(inputString)
                layoutSecond.visibility = View.GONE
            }
            "password" -> {
                itemCheck.visibility = View.GONE
                builder.setTitle(activity.baseContext.getString(R.string.change_password))
                positiveText = activity.baseContext.getString(R.string.update)

                layoutFirst.hint = activity.baseContext.getString(R.string.input_new_password)
                itemTextFirst.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                layoutFirst.endIconMode = END_ICON_PASSWORD_TOGGLE
                layoutFirst.setEndIconTintList(ContextCompat.getColorStateList(activity, R.color.theme_primary))
                layoutSecond.visibility = View.GONE
            }
            "delete" -> {
                itemCheck.visibility = View.GONE
                builder.setTitle(activity.baseContext.getString(R.string.delete_action))
                builder.setMessage(activity.baseContext.getString(R.string.cannot_undone))
                positiveText = activity.baseContext.getString(R.string.yes)

                layoutFirst.visibility = View.GONE
                layoutSecond.visibility = View.GONE
            }
            else -> positiveText = activity.baseContext.getString(R.string.save)
        }

        builder.setView(alertlayout)

        builder.setPositiveButton(positiveText) { _, _ ->
            listener?.onPositiveButtonClick(itemTextFirst.text.toString(),
                itemTextSecond.text.toString(),
                itemCheck.isChecked)
        }
        builder.setNeutralButton(activity.baseContext.getString(R.string.cancel)) { _, _ -> }
        builder.setCancelable(true)

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        when(inputType){
            "category" -> {
                itemTextFirst.addTextChangedListener {
                    if(it.toString().isEmpty()){
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                        layoutFirst.error = activity.baseContext.getString(R.string.category_empty)
                    }
                    else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                        layoutFirst.error = null
                    }
                }
            }
            "subTask" -> {
                itemTextFirst.addTextChangedListener {
                    if(it.toString().isEmpty()){
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                        layoutFirst.error = activity.baseContext.getString(R.string.sub_task_empty)
                    }
                    else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                        layoutFirst.error = null
                    }
                }
            }
            "anon" -> {
                itemTextFirst.addTextChangedListener {
                    if(!TextUtils.isEmpty(it.toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()){
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                        layoutFirst.error = null
                    }
                    else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                        layoutFirst.error = activity.baseContext.getString(R.string.input_valid_email)
                    }
                }
                itemTextSecond.addTextChangedListener {
                    if(it.toString().length >= 6){
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                        layoutSecond.error = null
                    }
                    else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                        layoutSecond.error = activity.baseContext.getString(R.string.input_valid_password)
                    }
                }
            }
            "email" -> {
                itemTextFirst.addTextChangedListener {
                    if(it.toString() == inputString.toString()){
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                        layoutFirst.error = activity.baseContext.getString(R.string.input_different_email)
                    }
                    else if(!TextUtils.isEmpty(it.toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()){
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                        layoutFirst.error = null
                    }
                    else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                        layoutFirst.error = activity.baseContext.getString(R.string.input_valid_email)
                    }
                }
            }
            "password" -> {
                itemTextFirst.addTextChangedListener {
                    if(it.toString().length >= 6){
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                        layoutFirst.error = null
                    }
                    else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                        layoutFirst.error = activity.baseContext.getString(R.string.input_valid_password)
                    }
                }
            }
            "delete" -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
            }
        }
    }
}