package com.developers.contentproviders

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.provider.ProviderTestRule
import com.developers.contentproviders.data.Villains
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Integration tests for VillainProvider ContentProvider
 * These tests run with the actual ContentProvider implementation
 */
@RunWith(AndroidJUnit4::class)
class VillainProviderIntegrationTest {

    @get:Rule
    val providerRule = ProviderTestRule.Builder(VillainProvider::class.java, VillainProvider.AUTHORITY)
        .build()

    private lateinit var contentResolver: ContentResolver

    @Before
    fun setUp() {
        contentResolver = providerRule.resolver
    }

    @Test
    fun testProviderInsertion() {
        val values = ContentValues().apply {
            put(Villains.VILLAIN_NAME, "Integration Test Villain")
            put(Villains.VILLAIN_SERIES, "Integration Test Series")
        }

        val uri = contentResolver.insert(VillainProvider.VILLAINS_URI, values)
        assertNotNull("Insert should return a URI", uri)

        val id = ContentUris.parseId(uri!!)
        assertTrue("ID should be positive", id > 0)
    }

    @Test
    fun testProviderQuery() {
        // First insert a test villain
        val values = ContentValues().apply {
            put(Villains.VILLAIN_NAME, "Query Test Villain")
            put(Villains.VILLAIN_SERIES, "Query Test Series")
        }

        val insertUri = contentResolver.insert(VillainProvider.VILLAINS_URI, values)
        assertNotNull(insertUri)

        // Now query all villains
        val cursor: Cursor? = contentResolver.query(
            VillainProvider.VILLAINS_URI,
            null, null, null, null
        )

        assertNotNull("Query should return a cursor", cursor)
        cursor?.let {
            assertTrue("Cursor should have at least one row", it.count > 0)
            
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(Villains.VILLAIN_NAME)
                val seriesIndex = it.getColumnIndex(Villains.VILLAIN_SERIES)
                
                assertTrue("Name column should exist", nameIndex >= 0)
                assertTrue("Series column should exist", seriesIndex >= 0)
                
                val name = it.getString(nameIndex)
                val series = it.getString(seriesIndex)
                
                assertNotNull("Name should not be null", name)
                assertNotNull("Series should not be null", series)
            }
            it.close()
        }
    }

    @Test
    fun testProviderQueryById() {
        // Insert a test villain
        val values = ContentValues().apply {
            put(Villains.VILLAIN_NAME, "Specific Query Villain")
            put(Villains.VILLAIN_SERIES, "Specific Query Series")
        }

        val insertUri = contentResolver.insert(VillainProvider.VILLAINS_URI, values)
        assertNotNull(insertUri)

        // Query the specific villain
        val cursor: Cursor? = contentResolver.query(
            insertUri!!,
            null, null, null, null
        )

        assertNotNull("Query by ID should return a cursor", cursor)
        cursor?.let {
            assertEquals("Cursor should have exactly one row", 1, it.count)
            
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(Villains.VILLAIN_NAME)
                val name = it.getString(nameIndex)
                assertEquals("Specific Query Villain", name)
            }
            it.close()
        }
    }

    @Test
    fun testProviderUpdate() {
        // Insert a test villain
        val initialValues = ContentValues().apply {
            put(Villains.VILLAIN_NAME, "Original Name")
            put(Villains.VILLAIN_SERIES, "Original Series")
        }

        val insertUri = contentResolver.insert(VillainProvider.VILLAINS_URI, initialValues)
        assertNotNull(insertUri)

        // Update the villain
        val updateValues = ContentValues().apply {
            put(Villains.VILLAIN_NAME, "Updated Name")
            put(Villains.VILLAIN_SERIES, "Updated Series")
        }

        val updatedRows = contentResolver.update(insertUri!!, updateValues, null, null)
        assertEquals("Should update exactly one row", 1, updatedRows)

        // Verify the update
        val cursor: Cursor? = contentResolver.query(
            insertUri,
            null, null, null, null
        )

        assertNotNull(cursor)
        cursor?.let {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(Villains.VILLAIN_NAME)
                val seriesIndex = it.getColumnIndex(Villains.VILLAIN_SERIES)
                
                val name = it.getString(nameIndex)
                val series = it.getString(seriesIndex)
                
                assertEquals("Updated Name", name)
                assertEquals("Updated Series", series)
            }
            it.close()
        }
    }

    @Test
    fun testProviderDelete() {
        // Insert a test villain
        val values = ContentValues().apply {
            put(Villains.VILLAIN_NAME, "To Be Deleted")
            put(Villains.VILLAIN_SERIES, "Delete Test Series")
        }

        val insertUri = contentResolver.insert(VillainProvider.VILLAINS_URI, values)
        assertNotNull(insertUri)

        // Delete the villain
        val deletedRows = contentResolver.delete(insertUri!!, null, null)
        assertEquals("Should delete exactly one row", 1, deletedRows)

        // Verify the deletion
        val cursor: Cursor? = contentResolver.query(
            insertUri,
            null, null, null, null
        )

        assertNotNull(cursor)
        cursor?.let {
            assertEquals("Cursor should have no rows after deletion", 0, it.count)
            it.close()
        }
    }

    @Test
    fun testGetType() {
        val allVillainsType = contentResolver.getType(VillainProvider.VILLAINS_URI)
        assertEquals(
            "vnd.android.cursor.dir/com.developers.contentproviders.villains",
            allVillainsType
        )

        val singleVillainUri = ContentUris.withAppendedId(VillainProvider.VILLAINS_URI, 1)
        val singleVillainType = contentResolver.getType(singleVillainUri)
        assertEquals(
            "vnd.android.cursor.item/com.developers.contentproviders.villains",
            singleVillainType
        )
    }

    @Test
    fun testMultipleInsertionsAndQuery() {
        val villains = listOf(
            "Joker" to "Batman",
            "Lex Luthor" to "Superman",
            "Reverse Flash" to "The Flash"
        )

        // Insert multiple villains
        val insertedUris = mutableListOf<Uri>()
        villains.forEach { (name, series) ->
            val values = ContentValues().apply {
                put(Villains.VILLAIN_NAME, name)
                put(Villains.VILLAIN_SERIES, series)
            }
            
            val uri = contentResolver.insert(VillainProvider.VILLAINS_URI, values)
            assertNotNull("Insert should succeed for $name", uri)
            insertedUris.add(uri!!)
        }

        // Query all villains
        val cursor: Cursor? = contentResolver.query(
            VillainProvider.VILLAINS_URI,
            null, null, null, null
        )

        assertNotNull(cursor)
        cursor?.let {
            assertTrue("Should have at least ${villains.size} villains", it.count >= villains.size)
            it.close()
        }
    }
}
