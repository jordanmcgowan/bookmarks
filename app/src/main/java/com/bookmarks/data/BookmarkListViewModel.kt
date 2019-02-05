package com.bookmarks.data

import com.bookmarks.BookmarkApplication
import com.bookmarks.networking.OpenLibraryApiService
import com.bookmarks.networking.toBookmark
import com.bookmarks.storage.Bookmark
import com.bookmarks.storage.BookmarkRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class BookmarkListViewModel(private val repository: BookmarkRepository) {

  var disposable: Disposable? = null
  val bookmarkListViewModel by lazy {
    BookmarkApplication.injectBookmarkListViewModel()
  }
  val api by lazy {
    OpenLibraryApiService.create()
  }

  fun getBookmarks(): Observable<BookmarkList> {
    return repository.getBookmarksFromDB()
      .map {
        Timber.v("Mapping bookmarks to UIData")
        BookmarkList(it, "Bookmarks")
      }.onErrorReturn {
        Timber.e(it)
        BookmarkList(emptyList(), "An error occured", it)
      }
  }

  fun addBookmark(isbn: String, addToDatabase: Boolean) {
    disposable = api.bookDetails(isbn)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe()
      {
        if (!it.items.isNullOrEmpty()){
          val book = it.items[0].volumeInfo.toBookmark(0)
          if (addToDatabase) {
            bookmarkListViewModel.addBookmark(book)
          }
        }
      }
  }

  fun addBookmark(bookmark: Bookmark) {
    repository.storeBookmarkInDB(bookmark)
  }

  fun deleteAllBookmarks() {
    repository.deleteAllBookmarks()
  }

  fun updateBookmark(bookmark: Bookmark) {
    repository.updateBookmark(bookmark)
  }

  fun deleteBookmark(bookmark: Bookmark) {
    repository.deleteBookmark(bookmark)
  }
}

data class BookmarkList(val bookmarks: List<Bookmark>, val message: String, val error: Throwable? = null)