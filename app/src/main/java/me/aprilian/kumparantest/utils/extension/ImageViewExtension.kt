package me.aprilian.kumparantest.utils.extension

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import me.aprilian.kumparantest.R


fun ImageView.load(source: Any){
    try {
        val validSource = if (source is String && source.contains("via.placeholder.com")){
            //add header to get valid request from "via.placeholder.com"
            GlideUrl(source, LazyHeaders.Builder()
                .addHeader("User-Agent", "Android")
                .build()
            )
        } else source

        Glide.with(this.context)
            .load(validSource)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(this);
    } catch (e: Exception){ }
}