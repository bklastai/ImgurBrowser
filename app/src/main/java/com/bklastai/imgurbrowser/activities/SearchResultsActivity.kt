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
import com.bklastai.imgurbrowser.controllers.SearchResultsAdapter
import com.bklastai.imgurbrowser.controllers.SearchResultsViewModel
import com.bklastai.imgurbrowser.networking.State
import kotlinx.android.synthetic.main.activity_search_results.*
import android.view.MenuItem
import com.bklastai.imgurbrowser.R
import java.lang.IllegalStateException

const val QUERY : String = "com.bklastai.imgurbrowser.QUERY"

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchResultsViewModel
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    private var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        setSupportActionBar(search_results_activity_toolbar)

        viewModel = ViewModelProviders.of(this).get(SearchResultsViewModel::class.java)
        initAdapter()
        updateState()
    }

    private fun initAdapter() {
        searchResultsAdapter = SearchResultsAdapter { viewModel.retry() }
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = searchResultsAdapter
    }

    private fun updateState() {
        txt_error.setOnClickListener { viewModel.retry() }
        if (viewModel.getState().hasObservers()) {
            viewModel.getState().removeObservers(this)
        }
        viewModel.getState().observe(this, Observer { state ->
            if (viewModel.listIsEmpty()) {
                var progressBarVisibility = View.GONE
                var textVisibility = View.VISIBLE
                when (state) {
                    State.ERROR -> txt_error.text = getString(R.string.error_txt_error)
                    State.NOT_STARTED -> txt_error.text = getString(R.string.error_txt_search_prompt)
                    State.LOADING -> {
                        progressBarVisibility = View.VISIBLE
                        textVisibility = View.GONE
                    }
                    State.DONE -> txt_error.text = getString(R.string.error_txt_no_results)
                    else -> throw IllegalStateException("State is null")
                }
                progress_bar.visibility = progressBarVisibility
                txt_error.visibility = textVisibility
            } else {
                progress_bar.visibility = View.GONE
                txt_error.visibility = View.GONE
            }
            searchResultsAdapter.setState(state ?: State.NOT_STARTED)
        })
    }

    override fun onNewIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            setCurrentQuery(intent.getStringExtra(SearchManager.QUERY))
        }
    }

    private fun setCurrentQuery(query: String) {
        if (query == currentQuery) return
        currentQuery = query
        viewModel.searchResults.removeObservers(this)
        viewModel.initSearchResults(currentQuery)
        viewModel.searchResults.observe(this, Observer {
            searchResultsAdapter.submitList(it)
        })
        updateState()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(QUERY, currentQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        setCurrentQuery(savedInstanceState?.getString(QUERY, "") ?: "")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_browser_activity, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.search)
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean { return true }
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                setCurrentQuery("")
                return true
            }
        })
        (searchMenuItem.actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    setCurrentQuery("")
                    return true
                }
            })
        }
        return true
    }
}