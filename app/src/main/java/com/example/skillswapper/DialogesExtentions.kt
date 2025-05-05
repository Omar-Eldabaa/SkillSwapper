package com.example.skillswapper

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment



fun Fragment.showMessage(
    message: String,
    title: String? = null,
    posActionName: String? = null,
    posAction: OnDialogActionClickListener? = null,
    negActionName: String? = null,
    neAction: OnDialogActionClickListener? = null,
    isCancelable: Boolean = true
): AlertDialog {
    val dialogBuilder = AlertDialog.Builder(context, R.style.ModernDialogStyle)
    dialogBuilder.setMessage(message)

    if (title != null) {
        dialogBuilder.setTitle(title)
    }

    if (posActionName != null) {
        dialogBuilder.setPositiveButton(posActionName) { dialog, _ ->
            dialog.dismiss()
            posAction?.onActionClick()
        }
    }

    if (negActionName != null) {
        dialogBuilder.setNegativeButton(negActionName) { dialog, _ ->
            dialog.dismiss()
            neAction?.onActionClick()
        }
    }

    dialogBuilder.setCancelable(isCancelable)
    val dialog = dialogBuilder.create()

    // تخصيص شكل الـ Dialog (مثل الخلفية)
    dialog.window?.setBackgroundDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.dialog_background) })

    return dialog.apply { show() }


//    // تخصيص الأزرار
//    dialog.setOnShowListener {
//        // تخصيص الزر الإيجابي
//        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
//        positiveButton.setBackgroundResource(R.drawable.button_background)  // تطبيق الشكل المخصص للزر
//        positiveButton.setTextColor(Color.WHITE)  // تغيير لون النص داخل الزر
//
//        // تخصيص الزر السلبي
//        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
//        negativeButton.setBackgroundResource(R.drawable.button_background)  // تطبيق الشكل المخصص للزر
//        negativeButton.setTextColor(Color.WHITE)  // تغيير لون النص داخل الزر
//    }


}




fun Activity.showMessage(
    message: String,
    title: String? = null,
    posActionName: String? = null,
    posAction: OnDialogActionClickListener? = null,
    negActionName: String? = null,
    neAction: OnDialogActionClickListener? = null,
    isCancelable: Boolean = true
): AlertDialog {
    val dialogBuilder = AlertDialog.Builder(this, R.style.ModernDialogStyle)
    dialogBuilder.setMessage(message)

    if (title != null) {
        dialogBuilder.setTitle(title)
    }

    if (posActionName != null) {
        dialogBuilder.setPositiveButton(posActionName) { dialog, _ ->
            dialog.dismiss()
            posAction?.onActionClick()
        }
    }

    if (negActionName != null) {
        dialogBuilder.setNegativeButton(negActionName) { dialog, _ ->
            dialog.dismiss()
            neAction?.onActionClick()
        }
    }

    dialogBuilder.setCancelable(isCancelable)
    val dialog = dialogBuilder.create()

    // تخصيص شكل الـ Dialog (مثل الخلفية)
    dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.dialog_background))

    return dialog.apply { show() }

//    // تخصيص الأزرار
//    dialog.setOnShowListener {
//        // تخصيص الزر الإيجابي
//        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
//        positiveButton.setBackgroundResource(R.drawable.button_background)  // تطبيق الشكل المخصص للزر
//        positiveButton.setTextColor(Color.WHITE)  // تغيير لون النص داخل الزر
//
//        // تخصيص الزر السلبي
//        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
//        negativeButton.setBackgroundResource(R.drawable.button_background)  // تطبيق الشكل المخصص للزر
//        negativeButton.setTextColor(Color.WHITE)  // تغيير لون النص داخل الزر
//    }



}



fun Activity.showLoadingProgressDialog(message: String ,isCancelable: Boolean=true):AlertDialog{
    val alertDialog = ProgressDialog(this)
    alertDialog.setMessage(message)
    alertDialog.setCancelable(isCancelable)
    return alertDialog

}
fun Fragment.showLoadingProgressDialog(message: String ,isCancelable: Boolean=true):AlertDialog{
    val alertDialog = ProgressDialog(requireContext())
    alertDialog.setMessage(message)
    alertDialog.setCancelable(isCancelable)
    return alertDialog

}