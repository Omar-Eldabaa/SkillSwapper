package com.example.skillswapper

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
@BindingAdapter("app:Error")
fun bindingErrorOnTextLayout(
    textInputLayout :TextInputLayout ,
    errorMessage:String?
            ){
    textInputLayout.error = errorMessage

}