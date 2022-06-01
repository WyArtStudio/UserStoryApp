package com.wahyuhw.userstoryapp.ui.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.wahyuhw.userstoryapp.R
import com.wahyuhw.userstoryapp.data.room.BookmarkStoryDao
import com.wahyuhw.userstoryapp.data.room.StoryDatabase
import com.wahyuhw.userstoryapp.utils.getBitmapFromURL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {
    private val mWidgetItems = ArrayList<Bitmap>()
    private lateinit var bookmarkStoryDao: BookmarkStoryDao

    override fun onCreate() {
        val database: StoryDatabase = StoryDatabase.getDatabase(mContext)
        bookmarkStoryDao = database.bookmarkStoryDao()
    }

    override fun onDataSetChanged() {
        CoroutineScope(Dispatchers.Default).launch {
            val list = bookmarkStoryDao.getListBookmarkStory()
            for (bookmarkStoryEntity in list) {
                val url = bookmarkStoryEntity.photoUrl
                val bitmap = getBitmapFromURL(url)
                if (bitmap != null) {
                    mWidgetItems.add(bitmap)
                }
            }
        }
    }

    override fun onDestroy() {}

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)

        // TODO: Load image
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(ImageStoryWidget.EXTRA_ITEM to position)
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.image_widget, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}