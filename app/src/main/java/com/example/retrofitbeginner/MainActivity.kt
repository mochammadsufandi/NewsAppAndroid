package com.example.retrofitbeginner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitbeginner.adapter.NewsAdapter
import com.example.retrofitbeginner.model.Article
import com.example.retrofitbeginner.model.NewsResponse
import com.example.retrofitbeginner.retrofit.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), NewsAdapter.OnItemClickListener {

    private val TAG = "Main Activity Log"
    lateinit var newsAdapter: NewsAdapter
    private var currentPage = 1
    private val pageSize = 20 // Adjust based on API limit
    private var isLoading = false
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()
        getDataFromAPI(currentPage)
    }

    private fun getDataFromAPI(page: Int) {
        showProgressBar()
        isLoading = true
        ApiService.endpoint.getHeadlinesNews(page = page, pageSize = pageSize).enqueue(object : Callback<NewsResponse> {
            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                printLog("Exception: ${t.message}")
                hideProgressBar()
                isLoading = false
            }

            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { newsResponse ->
                        val result = newsResponse.articles
                        printLog("Total Results: ${newsResponse.totalResults}")
                        showData(newsResponse)
                        isLastPage = result.size < pageSize
                    }
                } else {
                    printLog("Error: ${response.errorBody()?.string()}")
                }
                hideProgressBar()
                isLoading = false
            }
        })
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(arrayListOf(),this)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = newsAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= pageSize) {
                            currentPage++
                            getDataFromAPI(currentPage)
                        }
                    }
                }
            })
        }
    }

    private fun showProgressBar() {
        val paginationProgressBar = findViewById<View>(R.id.progressBar)
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        val paginationProgressBar = findViewById<View>(R.id.progressBar)
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun printLog(message: String) {
        Log.d(TAG, "printLog: $message")
    }

    private fun showData(data: NewsResponse) {
        val newsResults = data.articles
        newsAdapter.setData(newsResults)
    }

    override fun onItemClick(article: Article) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("article_data", article)
        startActivity(intent)
    }
}