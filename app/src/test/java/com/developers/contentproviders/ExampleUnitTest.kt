package com.developers.contentproviders

import org.junit.Test
import org.junit.Assert.*
import com.developers.contentproviders.data.Villains
import android.content.ContentValues

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testVillainFromContentValues() {
        val contentValues = ContentValues().apply {
            put(Villains.COLUMN_ID, 1L)
            put(Villains.VILLAIN_NAME, "Test Villain")
            put(Villains.VILLAIN_SERIES, "Test Series")
        }

        val villain = Villains.fromContentValues(contentValues)

        assertEquals(1L, villain.id)
        assertEquals("Test Villain", villain.villainName)
        assertEquals("Test Series", villain.villainSeries)
    }

    @Test
    fun testVillainDataClass() {
        val villain = Villains(
            villainName = "Joker",
            villainSeries = "Batman",
            id = 1L
        )

        assertEquals("Joker", villain.villainName)
        assertEquals("Batman", villain.villainSeries)
        assertEquals(1L, villain.id)
    }

    @Test
    fun testSampleVillainsData() {
        val sampleVillains = Villains.SAMPLE_VILLAINS
        
        assertTrue("Sample villains should not be empty", sampleVillains.isNotEmpty())
        assertEquals(5, sampleVillains.size)
        
        val firstVillain = sampleVillains.first()
        assertEquals("Joker", firstVillain.first)
        assertEquals("Batman", firstVillain.second)
    }
}
