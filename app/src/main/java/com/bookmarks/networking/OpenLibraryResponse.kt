package com.bookmarks.networking

import com.bookmarks.storage.Bookmark

object OpenLibraryResponse {
  data class Result(val items: List<BookItem>)
  data class BookItem(val volumeInfo: VolumeInfo)
  data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val publisher: String,
    val publishedDate: String,
    val pageCount: Int,
    val averageRating: Double,
    val imageLinks: Map<String, String>,
    val industryIdentifiers: List<ISBN>
  )
  data class ISBN(
    val type: String,
    val identifier: String
  )
}

fun OpenLibraryResponse.VolumeInfo.toBookmark(pagesRead: Int): Bookmark {
  return Bookmark(
    title = this.title,
    author = this.authors[0],
    publisher = this.publisher,
    publishedYear = this.publishedDate,
    numPages = this.pageCount,
    rating = this.averageRating,
    imageUrl = this.imageLinks["thumbnail"]!!,
    isbn = this.industryIdentifiers[0].identifier.toLong(),
    pagesRead = pagesRead
  )
}