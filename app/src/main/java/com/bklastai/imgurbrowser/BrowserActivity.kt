package com.bklastai.imgurbrowser

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject

const val imgurUrlForm = "https://api.imgur.com/3/gallery/search/time/@d?q=@s&q_type=png"

class BrowserActivity : AppCompatActivity() {
    private val resultsAdapter = SearchResultsAdapter(ArrayList())
    private var currentPage: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        setSupportActionBar(findViewById(R.id.browser_activity_toolbar))

        findViewById<RecyclerView>(R.id.results).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = resultsAdapter
        }
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            val url = imgurUrlForm.format(currentPage, query)
            val request = object : JsonObjectRequest(GET, url, null, Response.Listener<JSONObject> { response ->
                val success = response.optBoolean("success")
                if (success) {
                    resultsAdapter.addSearchResults(parseSearchResults(response.optJSONArray("data")))
                }
            }, Response.ErrorListener {
                it.printStackTrace()
            }) {
                override fun getHeaders(): Map<String, String> {
                    return try {
                        HashMap<String, String>().apply {
                            put("Authorization", "Client-ID 126701cd8332f32")
                        }
                    } catch (e: AuthFailureError) {
                        emptyMap()
                    }
                }
            }
            VolleySingleton.getInstance(this).addToRequestQueue(request)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_browser_activity, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        return true
    }

    private fun parseSearchResults(data: JSONArray?): ArrayList<SearchResult> {
        val result = ArrayList<SearchResult>()
        if (data == null) return result

        for(i in 0 until data.length()) {
            val datum = data.optJSONObject(i)
            val title = datum?.optString("title")
            val url = datum?.optString("link")
            if (!title.isNullOrEmpty() && !url.isNullOrEmpty()) {
                // should verify this, search result without a title might still be considered valid
                result.add(SearchResult(title, url))
            }
        }
        return result
    }
}
