package com.bookmarks.data

import com.bookmarks.storage.Bookmark
import com.bookmarks.storage.BookmarkRepository
import io.reactivex.Observable
import timber.log.Timber

class BookmarkListViewModel(private val repository: BookmarkRepository) {
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

  fun addBookmark(bookmark: Bookmark) {
    repository.storeBookmarkInDB(bookmark)
  }

  fun deleteAllBookmarks() {
    repository.deleteAllBookmarks()
  }
}

data class BookmarkList(val bookmarks: List<Bookmark>, val message: String, val error: Throwable? = null)