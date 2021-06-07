package me.aprilian.kumparantest.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.aprilian.kumparantest.data.Comment
import me.aprilian.kumparantest.data.Resource
import me.aprilian.kumparantest.databinding.ItemCommentBinding
import me.aprilian.kumparantest.ui.base.BaseRVAdapter

class CommentsAdapter(ctx: Context?, resource: Resource<List<Comment>>) : BaseRVAdapter<Comment>(ctx, resource) {

    class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun createDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return CommentViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CommentViewHolder) {
            holder.binding.comment = resource.data?.get(position)
            holder.binding.executePendingBindings()
        }
    }
}