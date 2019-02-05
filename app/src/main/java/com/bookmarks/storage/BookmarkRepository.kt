package com.bookmarks.storage

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class BookmarkRepository(val bookmarkDao: BookmarkDao) {


  fun getBookmarksFromDB(): Observable<List<Bookmark>> {
    return bookmarkDao.getBookmarks()
      .toObservable()
      .doOnComplete { Timber.v("Completed getting books") }
      .doOnNext { Timber.d("Dispatching ${it.size} bookmarks from DB...") }
      .doOnError { Timber.e(it) }
  }

  fun storeBookmarkInDB(bookmark: Bookmark) {
    Observable.fromCallable { bookmarkDao.insertBookmark(bookmark) }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .subscribe {
        Timber.d("Inserted ${bookmark.title} in DB...")
      }
  }

  fun deleteAllBookmarks() {
    Observable.fromCallable { bookmarkDao.deleteAllBookmarks() }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .subscribe {
        Timber.d("Deleted all bookmarks in DB...")
      }
  }

  fun updateBookmark(bookmark: Bookmark) {
    Observable.fromCallable {bookmarkDao.updateBookmark(bookmark) }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .subscribe {
        Timber.d("Updating ${bookmark.title}...")
      }
  }

  fun deleteBookmark(bookmark: Bookmark) {
    Observable.fromCallable {bookmarkDao.deleteBookmark(bookmark) }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .subscribe {
        Timber.d("Deleting ${bookmark.title}...")
      }
  }

}