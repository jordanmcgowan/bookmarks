package com.bookmarks.storage

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
  @PrimaryKey(autoGenerate = true)
  val isbn: Long,
  val title: String,
  val author: String,
  val publisher: String,
  val publishedYear: String,
  val numPages: Int,
  val imageUrl: String,
  val rating: Double,
  val pagesRead: Int
)