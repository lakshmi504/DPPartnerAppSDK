package com.dpdelivery.android.utils

import android.app.Application
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.dpdelivery.android.BuildConfig


fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun FragmentActivity.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Fragment.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, text, duration).show()
}

fun FragmentActivity.logv(msg: String) {
    if (BuildConfig.DEBUG)
        Log.v(this.javaClass.name, msg)
}

fun FragmentActivity.loge(msg: String) {
    if (BuildConfig.DEBUG)
        Log.e(this.javaClass.name, msg)
}

fun Fragment.logv(msg: String) {
    if (BuildConfig.DEBUG)
        Log.v(this.javaClass.name, msg)
}

fun Application.logv(msg: String) {
    if (BuildConfig.DEBUG)
        Log.v(this.javaClass.name, msg)
}

fun EditText.setDrawableRight(right: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(this.context, right), null)
}

fun TextView.setDrawableLeft(left: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this.context, left), null, null, null)
}

fun TextView.setDrawableRight(right: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(this.context, right), null)
}


