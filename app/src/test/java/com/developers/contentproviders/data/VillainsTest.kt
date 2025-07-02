package com.developers.contentproviders.data

import android.content.ContentValues
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Villains data class
 */
class VillainsTest {

    @Test
    fun `create villain with default id`() {
        val villain = Villains(
            villainName = "Test Villain",
            villainSeries = "Test Series"
        )
        
        assertEquals("Test Villain", villain.villainName)
        assertEquals("Test Series", villain.villainSeries)
        assertEquals(0L, villain.id)
    }

    @Test
    fun `create villain with specific id`() {
        val villain = Villains(
            villainName = "Test Villain",
            villainSeries = "Test Series",
            id = 123L
        )
        
        assertEquals("Test Villain", villain.villainName)
        assertEquals("Test Series", villain.villainSeries)
        assertEquals(123L, villain.id)
    }

    @Test
    fun `fromContentValues creates villain correctly`() {
        val contentValues = ContentValues().apply {
            put(Villains.COLUMN_ID, 42L)
            put(Villains.VILLAIN_NAME, "Joker")
            put(Villains.VILLAIN_SERIES, "Batman")
        }
        
        val villain = Villains.fromContentValues(contentValues)
        
        assertEquals(42L, villain.id)
        assertEquals("Joker", villain.villainName)
        assertEquals("Batman", villain.villainSeries)
    }

    @Test
    fun `fromContentValues with null values creates villain with defaults`() {
        val contentValues = ContentValues()
        
        val villain = Villains.fromContentValues(contentValues)
        
        assertEquals(0L, villain.id)
        assertEquals("", villain.villainName)
        assertEquals("", villain.villainSeries)
    }

    @Test
    fun `fromContentValues with partial values`() {
        val contentValues = ContentValues().apply {
            put(Villains.VILLAIN_NAME, "Harley Quinn")
        }
        
        val villain = Villains.fromContentValues(contentValues)
        
        assertEquals(0L, villain.id)
        assertEquals("Harley Quinn", villain.villainName)
        assertEquals("", villain.villainSeries)
    }

    @Test
    fun `constant values are correct`() {
        assertEquals("_id", Villains.COLUMN_ID)
        assertEquals("villains", Villains.TABLE_NAME)
        assertEquals("villain_name", Villains.VILLAIN_NAME)
        assertEquals("series", Villains.VILLAIN_SERIES)
    }

    @Test
    fun `sample villains list is not empty`() {
        assertTrue("Sample villains list should not be empty", 
                  Villains.SAMPLE_VILLAINS.isNotEmpty())
        assertEquals(5, Villains.SAMPLE_VILLAINS.size)
    }
}
