package me.aprilian.kumparantest.utils

import android.content.Context
import android.widget.Toast

object Utils {
    fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT){
        Toast.makeText(this, message, duration).show()
    }
}