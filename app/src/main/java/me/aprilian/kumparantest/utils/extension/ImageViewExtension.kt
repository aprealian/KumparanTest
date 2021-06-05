package me.aprilian.kumparantest.utils.extension

import android.widget.ImageView
import com.bumptech.glide.Glide
import me.aprilian.kumparantest.R

fun ImageView.load(source: Any){
    try {
        Glide.with(this.context)
            .load(source)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(this);
    } catch (e: Exception){ }
}