package com.bookmarks

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bookmarks.storage.Bookmark
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.bookmark_list_item.view.bookmarkAuthorTV
import kotlinx.android.synthetic.main.bookmark_list_item.view.bookmarkISBNTV
import kotlinx.android.synthetic.main.bookmark_list_item.view.bookmarkRatingTV
import kotlinx.android.synthetic.main.bookmark_list_item.view.bookmarkTitleTV
import kotlinx.android.synthetic.main.bookmark_list_item.view.pageCountTV
import kotlinx.android.synthetic.main.bookmark_list_item.view.progressBar
import kotlinx.android.synthetic.main.bookmark_list_item.view.thumbnailIV
import timber.log.Timber

class BookmarkAdapter(val bookmarks: List<Bookmark>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

  override fun getItemCount(): Int {
    return bookmarks.size
  }

  override fun onBindViewHolder(view: ViewHolder, position: Int) {
    view.titleTV.text = bookmarks[position].title
    view.authorTV.text = context.getString(R.string.book_by_author, bookmarks[position].author)
    view.isbnTV.text = bookmarks[position].isbn.toString()
    Glide.with(context).load(bookmarks[position].imageUrl).into(view.thumbnailIV)
    view.ratingTV.text = bookmarks[position].rating.toString()
    val progress = ((bookmarks[position].pagesRead!! * 1.0/bookmarks[position].numPages * 1.0)) * 100
    view.progressBar.progress = progress.toInt()
    view.pageCountTV.text = context.getString(R.string.book_progress_percentage, bookmarks[position].pagesRead, bookmarks[position].numPages)
  }

  override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(context).inflate(R.layout.bookmark_list_item, p0, false))
  }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
  // Holds the Views that will add each bookmark to
  val titleTV = view.bookmarkTitleTV
  val authorTV = view.bookmarkAuthorTV
  val isbnTV = view.bookmarkISBNTV
  val thumbnailIV = view.thumbnailIV
  val ratingTV = view.bookmarkRatingTV
  val progressBar = view.progressBar
  val pageCountTV = view.pageCountTV
}