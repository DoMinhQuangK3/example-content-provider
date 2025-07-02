package com.developers.contentproviders

import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.developers.contentproviders.data.Villains
import com.developers.contentproviders.data.VillainsDao
import com.developers.contentproviders.data.VillainsDatabase
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Unit tests for VillainProvider ContentProvider
 */
@RunWith(RobolectricTestRunner::class)
class VillainProviderTest {

    @Mock
    private lateinit var mockDatabase: VillainsDatabase

    @Mock
    private lateinit var mockDao: VillainsDao

    @Mock
    private lateinit var mockCursor: Cursor

    private lateinit var provider: VillainProvider

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        provider = VillainProvider()
        
        // Mock the database creation
        val mockStatic: MockedStatic<VillainsDatabase> = mockStatic(VillainsDatabase::class.java)
        mockStatic.`when`<VillainsDatabase> { 
            VillainsDatabase.getDatabase(any()) 
        }.thenReturn(mockDatabase)
        
        whenever(mockDatabase.villainDao()).thenReturn(mockDao)
        
        // Initialize the provider
        provider.onCreate()
        
        mockStatic.close()
    }

    @Test
    fun `onCreate returns true`() {
        val result = provider.onCreate()
        assertTrue(result)
    }

    @Test
    fun `query all villains returns cursor from dao`() {
        whenever(mockDao.selectAll()).thenReturn(mockCursor)
        
        val result = provider.query(
            VillainProvider.VILLAINS_URI,
            null, null, null, null
        )
        
        assertEquals(mockCursor, result)
        verify(mockDao).selectAll()
    }

    @Test
    fun `query specific villain returns cursor from dao`() {
        val villainId = 1L
        val uri = ContentUris.withAppendedId(VillainProvider.VILLAINS_URI, villainId)
        
        whenever(mockDao.selectById(villainId)).thenReturn(mockCursor)
        
        val result = provider.query(uri, null, null, null, null)
        
        assertEquals(mockCursor, result)
        verify(mockDao).selectById(villainId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `query with invalid uri throws exception`() {
        val invalidUri = Uri.parse("content://invalid.authority/invalid")
        
        provider.query(invalidUri, null, null, null, null)
    }

    @Test
    fun `getType returns correct mime type for all villains`() {
        val result = provider.getType(VillainProvider.VILLAINS_URI)
        
        assertEquals("vnd.android.cursor.dir/com.developers.contentproviders.villains", result)
    }

    @Test
    fun `getType returns correct mime type for single villain`() {
        val uri = ContentUris.withAppendedId(VillainProvider.VILLAINS_URI, 1L)
        
        val result = provider.getType(uri)
        
        assertEquals("vnd.android.cursor.item/com.developers.contentproviders.villains", result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getType with invalid uri throws exception`() {
        val invalidUri = Uri.parse("content://invalid.authority/invalid")
        
        provider.getType(invalidUri)
    }

    @Test
    fun `insert villain returns correct uri`() {
        val contentValues = ContentValues().apply {
            put(Villains.VILLAIN_NAME, "Test Villain")
            put(Villains.VILLAIN_SERIES, "Test Series")
        }
        
        val insertedId = 123L
        
        // Mock the runBlocking call
        whenever(mockDao.insert(any())).thenReturn(insertedId)
        
        val result = provider.insert(VillainProvider.VILLAINS_URI, contentValues)
        
        val expectedUri = ContentUris.withAppendedId(VillainProvider.VILLAINS_URI, insertedId)
        assertEquals(expectedUri, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `insert with null values throws exception`() {
        provider.insert(VillainProvider.VILLAINS_URI, null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `insert with item uri throws exception`() {
        val uri = ContentUris.withAppendedId(VillainProvider.VILLAINS_URI, 1L)
        val contentValues = ContentValues().apply {
            put(Villains.VILLAIN_NAME, "Test Villain")
            put(Villains.VILLAIN_SERIES, "Test Series")
        }
        
        provider.insert(uri, contentValues)
    }

    @Test
    fun `update villain returns correct count`() {
        val villainId = 1L
        val uri = ContentUris.withAppendedId(VillainProvider.VILLAINS_URI, villainId)
        val contentValues = ContentValues().apply {
            put(Villains.VILLAIN_NAME, "Updated Villain")
            put(Villains.VILLAIN_SERIES, "Updated Series")
        }
        
        val expectedCount = 1
        whenever(mockDao.update(any())).thenReturn(expectedCount)
        
        val result = provider.update(uri, contentValues, null, null)
        
        assertEquals(expectedCount, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `update with null values throws exception`() {
        val uri = ContentUris.withAppendedId(VillainProvider.VILLAINS_URI, 1L)
        
        provider.update(uri, null, null, null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `update with all villains uri throws exception`() {
        val contentValues = ContentValues().apply {
            put(Villains.VILLAIN_NAME, "Updated Villain")
            put(Villains.VILLAIN_SERIES, "Updated Series")
        }
        
        provider.update(VillainProvider.VILLAINS_URI, contentValues, null, null)
    }

    @Test
    fun `delete villain returns correct count`() {
        val villainId = 1L
        val uri = ContentUris.withAppendedId(VillainProvider.VILLAINS_URI, villainId)
        
        val expectedCount = 1
        whenever(mockDao.deleteById(villainId)).thenReturn(expectedCount)
        
        val result = provider.delete(uri, null, null)
        
        assertEquals(expectedCount, result)
        verify(mockDao).deleteById(villainId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `delete with all villains uri throws exception`() {
        provider.delete(VillainProvider.VILLAINS_URI, null, null)
    }

    @Test
    fun `authority constant is correct`() {
        assertEquals("com.developers.contentproviders", VillainProvider.AUTHORITY)
    }

    @Test
    fun `base uri is correct`() {
        val expectedUri = Uri.parse("content://com.developers.contentproviders")
        assertEquals(expectedUri, VillainProvider.BASE_URI)
    }

    @Test
    fun `villains uri is correct`() {
        val expectedUri = Uri.parse("content://com.developers.contentproviders/villains")
        assertEquals(expectedUri, VillainProvider.VILLAINS_URI)
    }
}
