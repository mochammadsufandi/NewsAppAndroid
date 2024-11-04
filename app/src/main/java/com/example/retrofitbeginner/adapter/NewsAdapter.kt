package com.example.retrofitbeginner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.retrofitbeginner.R
import com.example.retrofitbeginner.model.Article
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class NewsAdapter(
    val news : MutableList<Article>,
    private val listener : OnItemClickListener
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_news,parent,false))
    }

    override fun getItemCount(): Int {
        return news.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = news[position]
        holder.titleText.text = data.title
        holder.sourceText.text = data.source.name
        holder.descriptionText.text = data.description
        holder.dateTimeText.text = formatDate(data.publishedAt)

        Glide.with(holder.view)
            .load(data.urlToImage)
            .placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_placeholder)
            .centerCrop()
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            listener.onItemClick(data)
        }
    }

    class ViewHolder (val view : View) : RecyclerView.ViewHolder(view) {
        val titleText : TextView = view.findViewById(R.id.articleTitle)
        val imageView : ImageView = view.findViewById(R.id.articleImage)
        val descriptionText : TextView = view.findViewById(R.id.articleDescription)
        val sourceText : TextView = view.findViewById(R.id.articleSource)
        val dateTimeText : TextView = view.findViewById(R.id.articleDateTime)
    }

    fun setData(data : List<Article>) {
        news.clear()
        news.addAll(data)
        notifyDataSetChanged()
    }

    private fun formatDate(inputDate: String): String {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputFormatter.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        val date = inputFormatter.parse(inputDate)
        return outputFormatter.format(date?:"")
    }

    interface OnItemClickListener {
        fun onItemClick(article: Article)
    }


}