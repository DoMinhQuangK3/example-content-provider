package com.developers.contentproviders.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.content.ContentValues
import android.provider.BaseColumns

/**
 * Villains entity representing a villain character from various series
 */
@Entity(tableName = Villains.TABLE_NAME)
data class Villains(
    @ColumnInfo(name = VILLAIN_NAME)
    val villainName: String,
    
    @ColumnInfo(name = VILLAIN_SERIES)
    val villainSeries: String,
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = COLUMN_ID)
    val id: Long = 0
) {
    companion object {
        const val COLUMN_ID = BaseColumns._ID
        const val TABLE_NAME = "villains"
        const val VILLAIN_NAME = "villain_name"
        const val VILLAIN_SERIES = "series"
        
        // Sample data for initial population
        val SAMPLE_VILLAINS = listOf(
            "Joker" to "Batman",
            "DeathStroke" to "Arrow", 
            "Reverse Flash" to "Flash",
            "Lex Luthor" to "Superman",
            "Harley Quinn" to "Suicide Squad"
        )
        
        /**
         * Creates a Villains object from ContentValues
         */
        fun fromContentValues(values: ContentValues): Villains {
            val id = values.getAsLong(COLUMN_ID) ?: 0L
            val name = values.getAsString(VILLAIN_NAME) ?: ""
            val series = values.getAsString(VILLAIN_SERIES) ?: ""
            
            return Villains(
                villainName = name,
                villainSeries = series,
                id = id
            )
        }
    }
}