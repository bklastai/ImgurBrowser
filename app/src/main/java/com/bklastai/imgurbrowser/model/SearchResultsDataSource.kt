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

class SearchResultsDataSource(
    private val networkService: NetworkService,
    private val compositeDisposable: CompositeDisposable)
    : PageKeyedDataSource<Int, SearchResult>() {

    var state: MutableLiveData<State> = MutableLiveData()
    private var retryCompletable: Completable? = null


    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, SearchResult>) {
        updateState(State.LOADING)
        compositeDisposable.add(networkService.getImages("all", 1, "cats",  "png", "Client-ID 126701cd8332f32")
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
        updateState(State.LOADING)
        compositeDisposable.add(networkService.getImages("all", params.key, "cats",  "png", "Client-ID 126701cd8332f32")
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

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, SearchResult>) {
    }

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