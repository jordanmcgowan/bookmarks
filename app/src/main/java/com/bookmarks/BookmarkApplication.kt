package com.bookmarks

import android.app.Application
import android.arch.persistence.room.Room
import com.bookmarks.data.BookmarkListViewModel
import com.bookmarks.storage.AppDatabase
import com.bookmarks.storage.BookmarkRepository
import com.facebook.stetho.Stetho
import timber.log.Timber

class BookmarkApplication: Application() {


  //For the sake of simplicity, for now we use this instead of Dagger
  companion object {
    private lateinit var userRepository: BookmarkRepository
    private lateinit var bookmarkListViewModel: BookmarkListViewModel
    private lateinit var appDatabase: AppDatabase

    fun injectBookmarkListViewModel() = bookmarkListViewModel

    fun injectUserDao() = appDatabase.bookmarkDao()
  }

  override fun onCreate() {
    super.onCreate()
    Timber.uprootAll()
    Timber.plant(Timber.DebugTree())

    Stetho.initializeWithDefaults(this)

    appDatabase = Room.databaseBuilder(applicationContext,
      AppDatabase::class.java, "bookmarks").build()

    userRepository = BookmarkRepository(appDatabase.bookmarkDao())
    bookmarkListViewModel = BookmarkListViewModel(userRepository)
  }
}