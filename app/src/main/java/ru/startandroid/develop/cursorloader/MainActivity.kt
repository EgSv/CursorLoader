package ru.startandroid.develop.cursorloader

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager.LoaderCallbacks
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import java.util.concurrent.TimeUnit

class MainActivity : FragmentActivity(), LoaderCallbacks<Cursor> {

    private var CM_DELETE_ID = 1
    private var lvData: ListView? = null
    private var db: DB? = null
    private var scAdapter: SimpleCursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DB(this)
        db!!.open()

        val from = arrayOf(DB.COLUMN_IMG, DB.COLUMN_TXT )
        val to = intArrayOf(R.id.ivImg, R.id.tvText)

        scAdapter = SimpleCursorAdapter(this, R.layout.item, null, from, to, 0)
        lvData = findViewById(R.id.lvData)
        lvData!!.adapter = scAdapter

        registerForContextMenu(lvData)
        supportLoaderManager.initLoader(0, null, this)
    }

    fun onButtonClick(view: View) {
        db!!.addRec("sometext " + (scAdapter!!.count + 1), R.drawable.ic_launcher)
        supportLoaderManager.getLoader<Any>(0)!!.forceLoad()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu!!.add(0, CM_DELETE_ID, 0, R.string.delete_record)
    }

    fun onContentItemSelected(item: MenuItem): Boolean {
        if (item.itemId == CM_DELETE_ID) {
            val acmi = item
                .menuInfo as AdapterContextMenuInfo
            db!!.delRec(acmi.id)
            supportLoaderManager.getLoader<Any>(0)!!.forceLoad()
            return true
        }
        return super.onContextItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        db!!.close()
    }

    override fun onCreateLoader(id: Int, bnd1: Bundle?): Loader<Cursor> {
        return MyCursorLoader(this, db)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        scAdapter!!.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
    }

    class MyCursorLoader(context: Context?, private val db: DB?): CursorLoader(context!!) {
        override fun loadInBackground(): Cursor {
            val cursor: Cursor = db!!.getAllData()
            try {
                TimeUnit.SECONDS.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return cursor
        }
    }
}