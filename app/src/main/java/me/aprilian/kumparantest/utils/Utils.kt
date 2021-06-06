package me.aprilian.kumparantest.utils

import android.content.Context
import android.widget.Toast

object Utils {
    fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT){
        Toast.makeText(this, message, duration).show()
    }

    fun pxToDp(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    fun dpToPx(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}