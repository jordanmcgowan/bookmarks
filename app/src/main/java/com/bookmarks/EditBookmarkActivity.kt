package com.bookmarks

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bookmarks.storage.Bookmark
import kotlinx.android.synthetic.main.activity_edit_bookmark.deleteBookmarkButton
import kotlinx.android.synthetic.main.activity_edit_bookmark.editAuthorET
import kotlinx.android.synthetic.main.activity_edit_bookmark.editBookmarkButton
import kotlinx.android.synthetic.main.activity_edit_bookmark.editIsbnET
import kotlinx.android.synthetic.main.activity_edit_bookmark.editPagesReadET
import kotlinx.android.synthetic.main.activity_edit_bookmark.editRatingET
import kotlinx.android.synthetic.main.activity_edit_bookmark.editTitleET

class EditBookmarkActivity : AppCompatActivity() {

  var bookmark: Bookmark? = null
  val BOOKMARK_KEY = "bookmark"
  val bookmarkListViewModel = BookmarkApplication.injectBookmarkListViewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_bookmark)
    editBookmarkButton.setOnClickListener {
      editBookmark()
    }
    deleteBookmarkButton.setOnClickListener {
      deleteBookmark()
    }
    bookmark = intent.getSerializableExtra(BOOKMARK_KEY) as Bookmark
    setupBookInteractions()
  }

  fun setupBookInteractions() {
    editIsbnET.hint = getString(R.string.isbn_prefix, bookmark!!.isbn)
    editTitleET.hint = getString(R.string.title_prefix, bookmark!!.title)
    editAuthorET.hint = getString(R.string.author_prefix, bookmark!!.author)
    editPagesReadET.hint = getString(R.string.pages_read, bookmark!!.numPages)
    editRatingET.hint = getString(R.string.rating_prefix, bookmark!!.rating)
  }

  fun editBookmark() {
    val isbn = if (!editIsbnET.text.isNullOrEmpty()) {
      editIsbnET.text.toString().toLong()
    } else {
      bookmark!!.isbn
    }
    val author = if (!editAuthorET.text.isNullOrEmpty()) {
      editAuthorET.text.toString()
    } else {
      bookmark!!.author
    }
    val title = if (!editTitleET.text.isNullOrEmpty()) {
      editTitleET.text.toString()
    } else {
      bookmark!!.title
    }
    val pagesRead = if (!editPagesReadET.text.isNullOrEmpty()) {
      editPagesReadET.text.toString().toIntOrNull()
    } else {
      bookmark!!.pagesRead
    }
    val rating = if (!editRatingET.text.isNullOrEmpty()) {
      editRatingET.text.toString().toDoubleOrNull()
    } else {
      bookmark!!.rating
    }


    val newBookmark = bookmark!!.copy(isbn = isbn, author = author, title = title, pagesRead = pagesRead ?: 0, rating = rating?: 0.0)
    if (bookmark!!.isbn == isbn) {
      bookmarkListViewModel.updateBookmark(newBookmark)
      Toast.makeText(this, "Updated entry for ${bookmark!!.title}", Toast.LENGTH_SHORT).show()
      returnToHome()
    } else {
      bookmarkListViewModel.deleteBookmark(bookmark!!)
      bookmarkListViewModel.addBookmark(newBookmark)
      Toast.makeText(this, "Replaced ${bookmark!!.title} with ${newBookmark.title}", Toast.LENGTH_SHORT).show()
      returnToHome()
    }
  }

  fun deleteBookmark() {
    bookmarkListViewModel.deleteBookmark(bookmark!!)
    Toast.makeText(this, "Deleted ${bookmark!!.title}", Toast.LENGTH_SHORT).show()
    returnToHome()
  }

  fun returnToHome() {
    val intent = Intent(this, HomeActivity::class.java)
    startActivity(intent)
  }
}