package com.bklastai.imgurbrowser.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bklastai.imgurbrowser.R
import com.bklastai.imgurbrowser.controllers.SearchResultsAdapter
import com.bklastai.imgurbrowser.controllers.SearchResultsViewModel
import com.bklastai.imgurbrowser.networking.State
import kotlinx.android.synthetic.main.activity_search_results.*

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchResultsViewModel
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        setSupportActionBar(search_results_activity_toolbar)

        viewModel = ViewModelProviders.of(this).get(SearchResultsViewModel::class.java)
        initAdapter()
        initState()
    }

    private fun initAdapter() {
        searchResultsAdapter = SearchResultsAdapter { viewModel.retry() }
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = searchResultsAdapter
        viewModel.searchResultList.observe(this, Observer {
            searchResultsAdapter.submitList(it)
        })
    }

    private fun initState() {
        txt_error.setOnClickListener { viewModel.retry() }
        viewModel.getState().observe(this, Observer { state ->
            progress_bar.visibility = if (viewModel.listIsEmpty() && state == State.LOADING) View.VISIBLE else View.GONE
            txt_error.visibility = if (viewModel.listIsEmpty() && state == State.ERROR) View.VISIBLE else View.GONE
            if (!viewModel.listIsEmpty()) {
                searchResultsAdapter.setState(state ?: State.DONE)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_browser_activity, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        return true
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
        }
    }
}