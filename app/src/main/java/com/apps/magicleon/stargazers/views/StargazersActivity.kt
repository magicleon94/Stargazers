package com.apps.magicleon.stargazers.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.apps.magicleon.stargazers.R
import com.apps.magicleon.stargazers.data.StargazersAdapter
import com.apps.magicleon.stargazers.data.StargazersRequest
import com.apps.magicleon.stargazers.models.StargazersResponse
import com.apps.magicleon.stargazers.utils.Utils
import kotlinx.android.synthetic.main.activity_stargazers.*
import java.util.concurrent.locks.ReentrantLock

class StargazersActivity : AppCompatActivity() {
    private lateinit var requestUsername: String
    private lateinit var requestRepository: String
    private var _stargazersAdapter = StargazersAdapter(this)
    private val _requestTag: String = "STARGAZERS_REQUEST"
    private lateinit var requestQueue: RequestQueue
    private var _currentPage = 1
    private var _moreToLoad = true
    private var _loading = false
    private val _lock = ReentrantLock()
    private val linearLayoutManager = LinearLayoutManager(this)

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = linearLayoutManager.childCount
            val totalItemCount = linearLayoutManager.itemCount
            val firstVisibleItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && _moreToLoad) {
                recyclerView.post {
                    makeRequest()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stargazers)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requestUsername = intent.getStringExtra(getString(R.string.USERNAME_KEY))
        requestRepository = intent.getStringExtra(getString(R.string.REPOSITORY_KEY))

        requestQueue = Volley.newRequestQueue(this)

        rv_stargazers_list.layoutManager = linearLayoutManager
        rv_stargazers_list.adapter = _stargazersAdapter

        rv_stargazers_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv_stargazers_list.addOnScrollListener(onScrollListener)

        if (savedInstanceState != null) {
            _stargazersAdapter.restoreSavedInstanceState(savedInstanceState)
            val scrollPosition = savedInstanceState.getInt(getString(R.string.scroll_position_key))
            rv_stargazers_list.scrollToPosition(scrollPosition)

            progressbar.visibility = savedInstanceState.getInt(getString(R.string.progress_bar_visibility_key))
            tv_no_stargazers.visibility = savedInstanceState.getInt(getString(R.string.tv_no_stargazers_visibility_key))
            result_group.visibility = savedInstanceState.getInt(getString(R.string.result_group_visibility_key))
            tv_error.visibility = savedInstanceState.getInt(getString(R.string.tv_error_visibility))

            stargazers_list_header.text =
                    savedInstanceState.getCharSequence(getString(R.string.stargazers_list_header_text_key))
            tv_error.text = savedInstanceState.getCharSequence(getString(R.string.tv_error_text_key))


            _currentPage = savedInstanceState.getInt(getString(R.string.current_page_key))
            _moreToLoad = savedInstanceState.getBoolean(getString(R.string.more_to_load_key))

        } else {
            makeRequest()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        _stargazersAdapter.saveInstanceState(outState!!)

        val scrollPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
        outState.putInt(getString(R.string.scroll_position_key), scrollPosition)

        outState.putInt(getString(R.string.progress_bar_visibility_key), progressbar.visibility)
        outState.putInt(getString(R.string.tv_no_stargazers_visibility_key), tv_no_stargazers.visibility)
        outState.putInt(getString(R.string.result_group_visibility_key), result_group.visibility)
        outState.putInt(getString(R.string.tv_error_visibility), tv_error.visibility)

        outState.putCharSequence(getString(R.string.stargazers_list_header_text_key), stargazers_list_header.text)
        outState.putCharSequence(getString(R.string.tv_error_text_key), tv_error.text)

        outState.putInt(getString(R.string.current_page_key), _currentPage)
        outState.putBoolean(getString(R.string.more_to_load_key), _moreToLoad)
    }

    override fun onDestroy() {
        super.onDestroy()
        requestQueue.cancelAll(_requestTag)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showError(error: VolleyError) {
        progressbar.visibility = View.GONE

        _lock.lock()
        _loading = false
        _stargazersAdapter.showLoading(false)
        _stargazersAdapter.notifyDataSetChanged()
        _lock.unlock()

        val errorMessage: String = when (error) {
            is NetworkError -> getString(R.string.no_connection_error_message)
            is ClientError -> getString(
                R.string.user_or_repo_not_found_error_message,
                requestUsername,
                requestRepository
            )
            is ServerError -> getString(R.string.server_error_message)
            is NoConnectionError -> getString(R.string.no_connection_error_message)
            is TimeoutError -> getString(R.string.connection_timeout_error_message)
            else -> getString(R.string.generic_error)
        }

        if (_currentPage == 1) {
            tv_error.text = errorMessage
            tv_error.visibility = View.VISIBLE
            result_group.visibility = View.GONE
        } else {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeRequest() {
        _lock.lock()
        if (!_loading) {
            val requestURLString =
                Utils.buildStargazersRequestURLString(requestUsername, requestRepository, _currentPage)
            val stargazersCustomRequest = StargazersRequest(
                Request.Method.GET, requestURLString,
                Response.Listener { response -> manageResponse(response) },
                Response.ErrorListener { error -> showError(error) }
            )
            stargazersCustomRequest.tag = _requestTag

            requestQueue.add(stargazersCustomRequest)

            _loading = true

            _stargazersAdapter.showLoading(true)
            _stargazersAdapter.notifyDataSetChanged()

            _lock.unlock()
        } else {
            _lock.unlock()
        }

    }

    private fun manageResponse(response: StargazersResponse) {
        if (_currentPage == 1) {
            progressbar.visibility = View.GONE

            if (response.stargazers.size == 0) {
                tv_no_stargazers.visibility = View.VISIBLE
            } else {
                result_group.visibility = View.VISIBLE
                stargazers_list_header.text =
                        getString(R.string.stargazers_list_header, requestRepository, requestUsername)
            }
        }

        _stargazersAdapter.addAll(response.stargazers)
        _stargazersAdapter.notifyDataSetChanged()

        _moreToLoad = response.hasNextPage

        if (_moreToLoad) {
            _currentPage++
        }

        _lock.lock()

        _loading = false

        _stargazersAdapter.showLoading(false)
        _stargazersAdapter.notifyDataSetChanged()
        
        _lock.unlock()
    }
}
