package com.bookmarks

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bookmarks.storage.Bookmark
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.bookmarkHintTV
import kotlinx.android.synthetic.main.activity_home.booksReadRV
import kotlinx.android.synthetic.main.activity_home.mainToolbar
import timber.log.Timber

class HomeActivity : AppCompatActivity(), BookmarkAdapter.OnClickListener {

  val bookmarkListViewModel = BookmarkApplication.injectBookmarkListViewModel()
  val BOOKMARK_KEY = "bookmark"
  val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)
    setSupportActionBar(mainToolbar)
  }

  override fun onStart() {
    super.onStart()
    getBookmarks()
  }

  private fun getBookmarks() {
    bookmarkListViewModel.getBookmarks()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        Timber.v("Received $it bookmarks")
        showBookmarks(it.bookmarks)
      }, {
        Timber.e(it)
      })
  }

  private fun showBookmarks(bookmarks: List<Bookmark>) {
    if (!bookmarks.isNullOrEmpty()) {
      booksReadRV.let {
        it.visibility = View.VISIBLE
        it.layoutManager = LinearLayoutManager(this)
        it.adapter = BookmarkAdapter(bookmarks, this, this)
      }
      bookmarkHintTV.visibility = View.GONE
    } else {
      bookmarkHintTV.visibility = View.VISIBLE
      booksReadRV.visibility = View.GONE
    }

  }

  override fun onBookmarkClick(bookmark: Bookmark) {
    val intent = Intent(this, EditBookmarkActivity::class.java)
    intent.putExtra(BOOKMARK_KEY, bookmark)
    startActivity(intent)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?) =
    when (item!!.itemId) {
      R.id.deleteAllAction -> {
        bookmarkListViewModel.deleteAllBookmarks()
        val handler = Handler()
        handler.postDelayed({
          getBookmarks()
        }, 1000)
        true
      }
      R.id.addBookmarkAction -> {
        val intent = Intent(this, AddBookmarkActivity::class.java)
        startActivity(intent)
        true
      }
      R.id.scanCodeAction -> {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
          takePictureIntent.resolveActivity(packageManager)?.also {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
          }
        }
        true
      }
      else -> {
        super.onOptionsItemSelected(item)
      }
    }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
      val bitmapImage = data?.extras?.get("data") as Bitmap
      Timber.v("We found an image!")
      val detector = FirebaseVision.getInstance().visionBarcodeDetector
      val image = FirebaseVisionImage.fromBitmap(bitmapImage)
      detector.detectInImage(image)
        .addOnSuccessListener { barcodes ->
          // Task completed successfully
          if (barcodes.isEmpty()) {
            Toast.makeText(baseContext, "Didn't find any barcodes...", Toast.LENGTH_SHORT).show()
          } else {
            for (barcode in barcodes) {
              val isbn = barcode.rawValue
              Timber.v("Got $isbn from the barcode")
              bookmarkListViewModel.addBookmark(isbn!!, true)
              val handler = Handler()
              handler.postDelayed({
                getBookmarks()
              }, 1000)
            }
          }
        }
        .addOnFailureListener {
          // Task failed with an exception
          Timber.e(it)
        }
    }
  }
}
