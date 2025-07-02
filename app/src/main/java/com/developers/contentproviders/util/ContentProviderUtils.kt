package com.developers.contentproviders.util

import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.developers.contentproviders.VillainProvider
import com.developers.contentproviders.data.Villains

/**
 * Utility class for Content Provider operations
 * Provides convenient methods for CRUD operations through ContentResolver
 */
object ContentProviderUtils {

    private const val TAG = "ContentProviderUtils"

    /**
     * Insert a villain through Content Provider
     */
    fun insertVillain(
        contentResolver: ContentResolver,
        villainName: String,
        villainSeries: String
    ): Uri? {
        return try {
            val values = ContentValues().apply {
                put(Villains.VILLAIN_NAME, villainName)
                put(Villains.VILLAIN_SERIES, villainSeries)
            }
            
            val uri = contentResolver.insert(VillainProvider.VILLAINS_URI, values)
            Log.d(TAG, "Successfully inserted villain: $villainName")
            uri
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting villain", e)
            null
        }
    }

    /**
     * Query all villains through Content Provider
     */
    fun queryAllVillains(contentResolver: ContentResolver): List<Villains> {
        val villains = mutableListOf<Villains>()
        
        try {
            val cursor = contentResolver.query(
                VillainProvider.VILLAINS_URI,
                null, null, null, null
            )
            
            cursor?.use {
                while (it.moveToNext()) {
                    val villain = cursorToVillain(it)
                    villains.add(villain)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying villains", e)
        }
        
        return villains
    }

    /**
     * Update a villain through Content Provider
     */
    fun updateVillain(
        contentResolver: ContentResolver,
        villainId: Long,
        villainName: String,
        villainSeries: String
    ): Int {
        return try {
            val values = ContentValues().apply {
                put(Villains.VILLAIN_NAME, villainName) 
                put(Villains.VILLAIN_SERIES, villainSeries)
            }
            
            val uri = Uri.withAppendedPath(VillainProvider.VILLAINS_URI, villainId.toString())
            val rowsUpdated = contentResolver.update(uri, values, null, null)
            
            Log.d(TAG, "Updated $rowsUpdated rows for villain ID: $villainId")
            rowsUpdated
        } catch (e: Exception) {
            Log.e(TAG, "Error updating villain", e)
            0
        }
    }

    /**
     * Delete a villain through Content Provider
     */
    fun deleteVillain(contentResolver: ContentResolver, villainId: Long): Int {
        return try {
            val uri = Uri.withAppendedPath(VillainProvider.VILLAINS_URI, villainId.toString())
            val rowsDeleted = contentResolver.delete(uri, null, null)
            
            Log.d(TAG, "Deleted $rowsDeleted rows for villain ID: $villainId")
            rowsDeleted
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting villain", e)
            0
        }
    }

    /**
     * Convert Cursor to Villains object
     */
    private fun cursorToVillain(cursor: Cursor): Villains {
        val idIndex = cursor.getColumnIndexOrThrow(Villains.COLUMN_ID)
        val nameIndex = cursor.getColumnIndexOrThrow(Villains.VILLAIN_NAME)
        val seriesIndex = cursor.getColumnIndexOrThrow(Villains.VILLAIN_SERIES)
        
        return Villains(
            id = cursor.getLong(idIndex),
            villainName = cursor.getString(nameIndex),
            villainSeries = cursor.getString(seriesIndex)
        )
    }
}
