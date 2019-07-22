package com.bklastai.imgurbrowser.controllers

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.bklastai.imgurbrowser.model.SearchResultsDataSource
import com.bklastai.imgurbrowser.model.SearchResultsDataSourceFactory
import com.bklastai.imgurbrowser.networking.NetworkService
import com.bklastai.imgurbrowser.networking.SearchResult
import com.bklastai.imgurbrowser.networking.State
import io.reactivex.disposables.CompositeDisposable

class SearchResultsViewModel : ViewModel() {

    private val networkService = NetworkService.getService()
    lateinit var searchResults: LiveData<PagedList<SearchResult>>
    private val compositeDisposable = CompositeDisposable()
    private val pageSize = 5
    private lateinit var searchResultsDataSourceFactory: SearchResultsDataSourceFactory

    init {
        initSearchResults("")
    }


    fun getState(): LiveData<State> {
        return searchResultsDataSourceFactory.getState()
    }

    fun retry() {
        searchResultsDataSourceFactory.searchResultsDataSource.value?.retry()
    }

    fun listIsEmpty(): Boolean {
        return searchResults.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun initSearchResults(query: String) {
        searchResultsDataSourceFactory = SearchResultsDataSourceFactory(compositeDisposable, networkService, query)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(true)
            .build()
        searchResults = LivePagedListBuilder(searchResultsDataSourceFactory, config).build()
    }
}