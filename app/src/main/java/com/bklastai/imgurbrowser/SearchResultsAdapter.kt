package com.bklastai.imgurbrowser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

data class SearchResult(val title: String, val url: String)

class SearchResultsAdapter(var searchResults: ArrayList<SearchResult>) :
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder>() {

    inner class SearchResultViewHolder(cardView: View, val imageView: ImageView, val titleView: TextView) :
        RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val container = LayoutInflater.from(parent.context).inflate(R.layout.search_resul_item, parent, false)
        val imageView: ImageView = container.findViewById(R.id.search_result_item_image)
        val titleView: TextView = container.findViewById(R.id.search_result_item_title)
        return SearchResultViewHolder(container, imageView, titleView)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val searchResult = getItem(position) ?: return
        holder.titleView.text = searchResult.title
        Picasso.get().load(searchResult.url).into(holder.imageView)

    }

    private fun getItem(position: Int): SearchResult? {
        return if (position >= 0 && position < searchResults.size) {
            searchResults[position]
        } else {
            null
        }
    }

    fun addSearchResults(newSearchResults: ArrayList<SearchResult>) {
        searchResults.addAll(newSearchResults)
        notifyDataSetChanged()
    }

    override fun getItemCount() = searchResults.size
}