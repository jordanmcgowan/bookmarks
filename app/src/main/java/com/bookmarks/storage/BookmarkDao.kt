package com.bookmarks.storage

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface BookmarkDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertBookmark(bookmark: Bookmark)

  @Update
  fun updateBookmark(bookmark: Bookmark)

  @Delete
  fun deleteBookmark(bookmark: Bookmark)

  @Query("DELETE FROM bookmarks")
  fun deleteAllBookmarks()

  @Query("SELECT * FROM bookmarks")
  fun getBookmarks(): Single<List<Bookmark>>
}