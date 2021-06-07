package me.aprilian.kumparantest.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.aprilian.kumparantest.R
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.data.Resource
import me.aprilian.kumparantest.databinding.ItemPostBinding
import me.aprilian.kumparantest.ui.base.BaseRVAdapter

class PostAdapter(ctx: Context?, resource: Resource<List<Post>>, private val clickListener: (Post?) -> Unit) : BaseRVAdapter<Post>(ctx, resource, loadingLayout = R.layout.item_post_loading) {

    class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun createDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return PostViewHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            val post = resource.data?.get(position)
            holder.binding.post = post
            holder.binding.executePendingBindings()
            holder.itemView.setOnClickListener {
                clickListener(post)
            }
        }
    }
}