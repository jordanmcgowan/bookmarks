package com.bookmarks

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.bookmarks.data.BookmarkList
import kotlinx.android.synthetic.main.activity_home.*
import com.bookmarks.storage.Bookmark
import com.bookmarks.storage.BookmarkRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class HomeActivity : AppCompatActivity() {

  val bookmarkListViewModel = BookmarkApplication.injectBookmarkListViewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)
    setSupportActionBar(mainToolbar)
  }

  override fun onStart() {
    super.onStart()
    bookmarkListViewModel.getBookmarks()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        Timber.v("Received $it bookmarks")
        showBookmarks(it)
      }, {
        Timber.e(it)
      })
  }

  fun showBookmarks(bookmarks: BookmarkList) {
    booksReadRV.let {
      it.layoutManager = LinearLayoutManager(this)
      it.adapter = BookmarkAdapter(bookmarks.bookmarks, this)
    }

  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?) =
    when (item!!.itemId) {
      R.id.deleteAllAction -> {
        bookmarkListViewModel.deleteAllBookmarks()
        true
      }
      R.id.addBookmarkAction -> {
        val intent = Intent(this, AddBookmarkActivity::class.java)
        startActivity(intent)
        true
      }
      else -> {
        super.onOptionsItemSelected(item)
      }
    }
}
