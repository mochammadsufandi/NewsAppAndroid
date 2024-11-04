package com.example.retrofitbeginner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitbeginner.adapter.NewsAdapter
import com.example.retrofitbeginner.model.Article
import com.example.retrofitbeginner.model.NewsResponse
import com.example.retrofitbeginner.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), NewsAdapter.OnItemClickListener {

    private val TAG = "Main Activity Log"
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var recyclerView : RecyclerView
    private var cachedArticle = mutableListOf<Article>()
    private var currentPage = 1
    private val pageSize = 7 // Adjust based on API limit
    private var totalData = 0
    private var scrollPosition = 0
    private var isScrollListened = false
    private var isLoading = false
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        getDataFromAPI(currentPage)
    }

    override fun onResume() {
        super.onResume()
        newsAdapter.setData(cachedArticle)
        recyclerView.scrollToPosition(scrollPosition)
    }

    override fun onPause() {
        super.onPause()
        scrollPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
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
                        totalData = newsResponse.totalResults
                        if(cachedArticle.size < totalData) {
                            cachedArticle.addAll(newsResponse.articles)
                            showData(cachedArticle)
                            newsAdapter.notifyItemRangeInserted(
                                cachedArticle.size - newsResponse.articles.size,
                                newsResponse.articles.size
                            )
                        }
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
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = newsAdapter

            if(!isScrollListened) {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                        scrollPosition = firstVisibleItemPosition

                        if (!isLoading && !isLastPage) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && totalItemCount >= pageSize
                                && firstVisibleItemPosition >= 0
                            ){
                                currentPage++
                                if(currentPage <= totalData/pageSize+1) {
                                    getDataFromAPI(currentPage)
                                } else {
                                    isLastPage = true
                                }
                            }
                        }
                    }
                })
                isScrollListened = true
            }
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

    private fun showData(data: MutableList<Article>) {
        val newsResults = data
        newsAdapter.setData(newsResults)
    }

    override fun onItemClick(article: Article) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("article_data", article)
        startActivity(intent)
    }
}