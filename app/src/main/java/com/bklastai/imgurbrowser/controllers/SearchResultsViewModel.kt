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
    var searchResultList: LiveData<PagedList<SearchResult>>
    private val compositeDisposable = CompositeDisposable()
    private val pageSize = 5
    private val searchResultsDataSourceFactory: SearchResultsDataSourceFactory

    init {
        searchResultsDataSourceFactory = SearchResultsDataSourceFactory(compositeDisposable, networkService)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()
        searchResultList = LivePagedListBuilder<Int, SearchResult>(searchResultsDataSourceFactory, config).build()
    }


    fun getState(): LiveData<State> = Transformations.switchMap<SearchResultsDataSource,
            State>(searchResultsDataSourceFactory.newsDataSourceLiveData, SearchResultsDataSource::state)

    fun retry() {
        searchResultsDataSourceFactory.newsDataSourceLiveData.value?.retry()
    }

    fun listIsEmpty(): Boolean {
        return searchResultList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}