package com.bklastai.imgurbrowser.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.bklastai.imgurbrowser.networking.NetworkService
import com.bklastai.imgurbrowser.networking.SearchResult
import com.bklastai.imgurbrowser.networking.State
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalStateException
import java.util.*

class SearchResultsDataSource(
    private val networkService: NetworkService,
    private val compositeDisposable: CompositeDisposable,
    private var query: String)
    : PageKeyedDataSource<Int, SearchResult>() {

    var state: MutableLiveData<State> = MutableLiveData()
    private var retryCompletable: Completable? = null


    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, SearchResult>) {
        if (state.value == State.LOADING) return
        if (query.isEmpty()) {
            updateState(State.NOT_STARTED)
            callback.onResult(
                emptyList(),
                null,
                1
            )
            return
        }
        updateState(State.LOADING)
        compositeDisposable.add(networkService.getImages(1, query,  "png", "Client-ID 126701cd8332f32")
                .subscribe(
                    { response ->
                        updateState(State.DONE)
                        callback.onResult(response.searchResults,
                            null,
                            2
                        )
                    },
                    {
                        updateState(State.ERROR)
                        setRetry(Action { loadInitial(params, callback) })
                    }
                )
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, SearchResult>) {
        if (state.value == State.LOADING) return
        if (query.isEmpty()) {
            throw IllegalStateException("Calling loadAfter when query is empty")
        }
        updateState(State.LOADING)
        compositeDisposable.add(networkService.getImages(params.key, query,  "png", "Client-ID 126701cd8332f32")
                .subscribe(
                    { response ->
                        updateState(State.DONE)
                        callback.onResult(response.searchResults,
                            params.key + 1
                        )
                    },
                    {
                        updateState(State.ERROR)
                        setRetry(Action { loadAfter(params, callback) })
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, SearchResult>) {}

    private fun updateState(state: State) {
        this.state.postValue(state)
    }

    fun retry() {
        if (retryCompletable != null) { compositeDisposable.add(retryCompletable!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
        }
    }

    private fun setRetry(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
    }
}