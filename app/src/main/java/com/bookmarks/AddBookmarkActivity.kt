package com.bookmarks

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bookmarks.networking.OpenLibraryApiService
import com.bookmarks.networking.toBookmark
import com.bookmarks.storage.Bookmark
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_bookmark.authorET
import kotlinx.android.synthetic.main.activity_add_bookmark.isbnET
import kotlinx.android.synthetic.main.activity_add_bookmark.pagesReadET
import kotlinx.android.synthetic.main.activity_add_bookmark.saveBookmarkButton
import kotlinx.android.synthetic.main.activity_add_bookmark.searchForBookButton
import kotlinx.android.synthetic.main.activity_add_bookmark.titleET
import timber.log.Timber

class AddBookmarkActivity : AppCompatActivity() {

  val api by lazy {
    OpenLibraryApiService.create()
  }
  var disposable: Disposable? = null
  val bookmarkListViewModel = BookmarkApplication.injectBookmarkListViewModel()
  var bookToAdd: Bookmark? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_bookmark)
    searchForBookButton.setOnClickListener { searchForBook() }
    saveBookmarkButton.setOnClickListener { saveBookmark() }
  }

  private fun searchForBook() {
    val isbn = isbnET.text.toString().trim()

    disposable = api.bookDetails(isbn)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(
        {
          Toast.makeText(this.applicationContext, "Autofilling for: " + it.items[0].volumeInfo.title, Toast.LENGTH_SHORT).show()
          val book = it.items[0].volumeInfo
          titleET.setText(book.title)
          authorET.setText(book.authors[0])
          pagesReadET.hint = baseContext.getString(R.string.pages_read, book.pageCount)
          saveBookmarkButton.isEnabled = true
          val pagesRead = if (pagesReadET.text.toString().isNotBlank()) pagesReadET.text.toString().toInt() else 0
          bookToAdd = book.toBookmark(pagesRead)
        },
        {
          Toast.makeText(this.applicationContext, "There was an error while finding your book", Toast.LENGTH_SHORT).show()
          saveBookmarkButton.isEnabled = false
          Timber.e(it)
        }
      )
  }

  fun saveBookmark() {
    if (bookToAdd != null) {
      val pagesRead = if (pagesReadET.text.toString().isNotBlank()) pagesReadET.text.toString().toInt() else 0
      val officialBookToAdd = bookToAdd!!.copy(pagesRead = pagesRead)
      bookmarkListViewModel.addBookmark(officialBookToAdd)
      Toast.makeText(this, "${officialBookToAdd.title} has been added", Toast.LENGTH_SHORT).show()
      val intent = Intent(this, HomeActivity::class.java)
      startActivity(intent)
    } else {
      Toast.makeText(this, "This book cannot be added at this time", Toast.LENGTH_SHORT).show()
    }

  }

}