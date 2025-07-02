package com.developers.contentproviders

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.developers.contentproviders.data.Villains
import com.developers.contentproviders.data.VillainsDatabase
import kotlinx.coroutines.runBlocking

/**
 * Content Provider for Villains data
 * Provides a standardized interface for accessing villain data across applications
 */
class VillainProvider : ContentProvider() {

    private lateinit var database: VillainsDatabase

    companion object {
        private const val TAG = "VillainProvider"
        const val AUTHORITY = "com.developers.contentproviders"
        val BASE_URI: Uri = Uri.parse("content://$AUTHORITY")
        val VILLAINS_URI: Uri = Uri.withAppendedPath(BASE_URI, Villains.TABLE_NAME)

        private const val CODE_VILLAINS_ALL = 1
        private const val CODE_VILLAIN_ITEM = 2

        private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, Villains.TABLE_NAME, CODE_VILLAINS_ALL)
            addURI(AUTHORITY, "${Villains.TABLE_NAME}/#", CODE_VILLAIN_ITEM)
        }
    }

    override fun onCreate(): Boolean {
        database = VillainsDatabase.getDatabase(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return when (URI_MATCHER.match(uri)) {
            CODE_VILLAINS_ALL -> {
                val cursor = database.villainDao().selectAll()
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }
            CODE_VILLAIN_ITEM -> {
                val id = ContentUris.parseId(uri)
                val cursor = database.villainDao().selectById(id)
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun getType(uri: Uri): String? {
        return when (URI_MATCHER.match(uri)) {
            CODE_VILLAINS_ALL ->
                "${ContentResolver.CURSOR_DIR_BASE_TYPE}/$AUTHORITY.${Villains.TABLE_NAME}"
            CODE_VILLAIN_ITEM ->
                "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/$AUTHORITY.${Villains.TABLE_NAME}"
            else ->
                throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (URI_MATCHER.match(uri)) {
            CODE_VILLAINS_ALL -> {
                if (values == null) {
                    throw IllegalArgumentException("ContentValues cannot be null")
                }

                val villain = Villains.fromContentValues(values)
                val id = runBlocking {
                    database.villainDao().insert(villain)
                }

                context?.contentResolver?.notifyChange(uri, null)
                ContentUris.withAppendedId(uri, id)
            }
            CODE_VILLAIN_ITEM -> {
                throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return when (URI_MATCHER.match(uri)) {
            CODE_VILLAINS_ALL -> {
                throw IllegalArgumentException("Invalid URI, cannot update without ID: $uri")
            }
            CODE_VILLAIN_ITEM -> {
                if (values == null) {
                    throw IllegalArgumentException("ContentValues cannot be null")
                }

                val id = ContentUris.parseId(uri)
                val villain = Villains.fromContentValues(values).copy(id = id)
                
                val count = runBlocking {
                    database.villainDao().update(villain)
                }

                context?.contentResolver?.notifyChange(uri, null)
                count
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return when (URI_MATCHER.match(uri)) {
            CODE_VILLAINS_ALL -> {
                throw IllegalArgumentException("Invalid URI, cannot delete without ID: $uri")
            }
            CODE_VILLAIN_ITEM -> {
                val id = ContentUris.parseId(uri)
                val count = runBlocking {
                    database.villainDao().deleteById(id)
                }

                context?.contentResolver?.notifyChange(uri, null)
                count
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }
}
