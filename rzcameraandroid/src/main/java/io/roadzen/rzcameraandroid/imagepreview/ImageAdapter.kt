package io.roadzen.rzcameraandroid.imagepreview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.roadzen.rzcameraandroid.R
import io.roadzen.rzcameraandroid.util.GlideApp
import io.roadzen.rzcameraandroid.util.LOG_TAG
import kotlinx.android.synthetic.main.list_item_image.view.*

class ImageAdapter(private val clickListener: (String) -> Unit) :
    ListAdapter<String, ImageAdapter.ViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.list_item_image, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(uriStr: String, clickListener: (String) -> Unit) {
            GlideApp.with(itemView).load(uriStr).into(itemView.imageView)
            itemView.setOnClickListener { clickListener(uriStr) }
        }
    }
}

class ImageDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}