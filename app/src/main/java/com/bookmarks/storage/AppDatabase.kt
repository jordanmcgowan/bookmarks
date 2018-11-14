package com.bookmarks.storage

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(Bookmark::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
  abstract fun bookmarkDao(): BookmarkDao
}