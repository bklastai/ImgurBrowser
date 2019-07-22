package com.bklastai.imgurbrowser.views

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bklastai.imgurbrowser.R
import com.bklastai.imgurbrowser.activities.ImageViewingActivity
import com.bklastai.imgurbrowser.networking.SearchResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.search_result_item.view.*

const val IMAGE_LINK  = "com.bklastai.imgurbrowser.views.IMAGE_LINK"
const val IMAGE_TITLE  = "com.bklastai.imgurbrowser.views.IMAGE_TITLE"

class SearchResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(searchResult: SearchResult?) {
        if (searchResult != null) {
            itemView.search_result_item_title.text = searchResult.title
            Picasso.get().load(searchResult.image).into(itemView.search_result_item_image)
            itemView.search_result_item_image.setOnClickListener {
                val intent = Intent(itemView.search_result_item_image.context, ImageViewingActivity::class.java).apply {
                    putExtra(IMAGE_TITLE, searchResult.title)
                    putExtra(IMAGE_LINK, searchResult.image)
                }
                itemView.search_result_item_image.context.startActivity(intent)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): SearchResultViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_result_item, parent, false)
            return SearchResultViewHolder(view)
        }
    }
}