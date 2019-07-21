package com.bklastai.imgurbrowser.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.bklastai.imgurbrowser.networking.NetworkService
import com.bklastai.imgurbrowser.networking.SearchResult
import io.reactivex.disposables.CompositeDisposable

class SearchResultsDataSourceFactory(
    private val compositeDisposable: CompositeDisposable,
    private val networkService: NetworkService)
    : DataSource.Factory<Int, SearchResult>() {

    val newsDataSourceLiveData = MutableLiveData<SearchResultsDataSource>()

    override fun create(): DataSource<Int, SearchResult> {
        val newsDataSource = SearchResultsDataSource(networkService, compositeDisposable)
        newsDataSourceLiveData.postValue(newsDataSource)
        return newsDataSource
    }
}