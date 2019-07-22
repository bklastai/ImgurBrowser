package com.bklastai.imgurbrowser.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import com.bklastai.imgurbrowser.networking.NetworkService
import com.bklastai.imgurbrowser.networking.SearchResult
import com.bklastai.imgurbrowser.networking.State
import io.reactivex.disposables.CompositeDisposable

class SearchResultsDataSourceFactory(
    private val compositeDisposable: CompositeDisposable,
    private val networkService: NetworkService,
    private var query: String)
    : DataSource.Factory<Int, SearchResult>() {

    val searchResultsDataSource = MutableLiveData<SearchResultsDataSource>()

    override fun create(): DataSource<Int, SearchResult> {
        val searchResultsDataSource = SearchResultsDataSource(networkService, compositeDisposable, query)
        this.searchResultsDataSource.postValue(searchResultsDataSource)
        return searchResultsDataSource
    }

    fun getState(): LiveData<State> {
        return Transformations.switchMap(searchResultsDataSource) {
            it.state
        }
    }

    fun setNewQuery(query: String) {
        this.query = query
        searchResultsDataSource.value?.invalidate()
    }
}