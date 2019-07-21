package com.bklastai.imgurbrowser.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bklastai.imgurbrowser.R
import com.bklastai.imgurbrowser.networking.SearchResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.search_result_item.view.*

class SearchResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(searchResult: SearchResult?) {
        if (searchResult != null) {
            itemView.search_result_item_title.text = searchResult.title
            Picasso.get().load(searchResult.image).into(itemView.search_result_item_image)
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