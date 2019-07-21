package com.bklastai.imgurbrowser.networking

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("data") val searchResults: List<SearchResult>
)

data class SearchResult(
    @SerializedName("title") val title: String,
    @SerializedName("link") val image: String
)

enum class State {
    DONE, LOADING, ERROR
}