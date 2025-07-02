package com.developers.contentproviders.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.IOException

/**
 * Integration tests for VillainsDatabase and VillainsDao
 * These tests use an in-memory database
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class VillainsDatabaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: VillainsDatabase
    private lateinit var villainDao: VillainsDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VillainsDatabase::class.java
        ).allowMainThreadQueries().build()
        
        villainDao = database.villainDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun testInsertAndGetVillain() = runTest {
        val villain = Villains("Test Villain", "Test Series", 1)
        
        val insertedId = villainDao.insert(villain)
        
        assertTrue("Inserted ID should be positive", insertedId > 0)
        
        val retrievedVillain = villainDao.getVillainById(insertedId)
        
        assertNotNull("Retrieved villain should not be null", retrievedVillain)
        assertEquals("Villain name should match", villain.villainName, retrievedVillain?.villainName)
        assertEquals("Villain series should match", villain.villainSeries, retrievedVillain?.villainSeries)
    }

    @Test
    fun testGetAllVillains() = runTest {
        val villains = listOf(
            Villains("Villain 1", "Series 1"),
            Villains("Villain 2", "Series 2"),
            Villains("Villain 3", "Series 3")
        )
        
        villains.forEach { villainDao.insert(it) }
        
        val allVillains = villainDao.getAllVillains()
        
        assertTrue("Should have at least 3 villains", allVillains.size >= 3)
        
        val names = allVillains.map { it.villainName }
        assertTrue("Should contain Villain 1", names.contains("Villain 1"))
        assertTrue("Should contain Villain 2", names.contains("Villain 2"))
        assertTrue("Should contain Villain 3", names.contains("Villain 3"))
    }

    @Test
    fun testGetAllVillainsFlow() = runTest {
        val villain = Villains("Flow Test Villain", "Flow Test Series")
        
        villainDao.insert(villain)
        
        val villainsFlow = villainDao.getAllVillainsFlow().first()
        
        assertTrue("Flow should contain at least one villain", villainsFlow.isNotEmpty())
        
        val names = villainsFlow.map { it.villainName }
        assertTrue("Flow should contain Flow Test Villain", names.contains("Flow Test Villain"))
    }

    @Test
    fun testUpdateVillain() = runTest {
        val originalVillain = Villains("Original Name", "Original Series")
        
        val insertedId = villainDao.insert(originalVillain)
        
        val updatedVillain = Villains("Updated Name", "Updated Series", insertedId)
        val updateCount = villainDao.update(updatedVillain)
        
        assertEquals("Should update exactly one row", 1, updateCount)
        
        val retrievedVillain = villainDao.getVillainById(insertedId)
        
        assertNotNull("Retrieved villain should not be null", retrievedVillain)
        assertEquals("Name should be updated", "Updated Name", retrievedVillain?.villainName)
        assertEquals("Series should be updated", "Updated Series", retrievedVillain?.villainSeries)
    }

    @Test
    fun testDeleteVillain() = runTest {
        val villain = Villains("To Be Deleted", "Delete Test Series")
        
        val insertedId = villainDao.insert(villain)
        
        val deleteCount = villainDao.deleteById(insertedId)
        
        assertEquals("Should delete exactly one row", 1, deleteCount)
        
        val retrievedVillain = villainDao.getVillainById(insertedId)
        
        assertNull("Villain should be null after deletion", retrievedVillain)
    }

    @Test
    fun testCount() = runTest {
        val initialCount = villainDao.count()
        
        val villain = Villains("Count Test Villain", "Count Test Series")
        villainDao.insert(villain)
        
        val newCount = villainDao.count()
        
        assertEquals("Count should increase by 1", initialCount + 1, newCount)
    }

    @Test
    fun testInsertAll() = runTest {
        val villains = listOf(
            Villains("Batch 1", "Series 1"),
            Villains("Batch 2", "Series 2"),
            Villains("Batch 3", "Series 3")
        )
        
        val initialCount = villainDao.count()
        
        villainDao.insertAll(villains)
        
        val newCount = villainDao.count()
        
        assertEquals("Count should increase by 3", initialCount + 3, newCount)
    }

    @Test
    fun testDeleteAll() = runTest {
        // Insert some test data
        val villains = listOf(
            Villains("Delete All 1", "Series 1"),
            Villains("Delete All 2", "Series 2")
        )
        
        villains.forEach { villainDao.insert(it) }
        
        val countBeforeDelete = villainDao.count()
        assertTrue("Should have at least 2 villains", countBeforeDelete >= 2)
        
        villainDao.deleteAll()
        
        val countAfterDelete = villainDao.count()
        assertEquals("Should have 0 villains after delete all", 0, countAfterDelete)
    }

    @Test
    fun testSelectById() = runTest {
        val villain = Villains("Select By ID Test", "Select Test Series")
        
        val insertedId = villainDao.insert(villain)
        
        val cursor = villainDao.selectById(insertedId)
        
        assertNotNull("Cursor should not be null", cursor)
        assertTrue("Cursor should have at least one row", cursor.count > 0)
        
        cursor.close()
    }

    @Test
    fun testSelectAll() = runTest {
        val villain = Villains("Select All Test", "Select All Series")
        
        villainDao.insert(villain)
        
        val cursor = villainDao.selectAll()
        
        assertNotNull("Cursor should not be null", cursor)
        assertTrue("Cursor should have at least one row", cursor.count > 0)
        
        cursor.close()
    }

    @Test
    fun testInsertWithConflictReplace() = runTest {
        val villain1 = Villains("Original", "Original Series", 1)
        val villain2 = Villains("Replacement", "Replacement Series", 1)
        
        villainDao.insert(villain1)
        villainDao.insert(villain2) // This should replace the first one
        
        val retrievedVillain = villainDao.getVillainById(1)
        
        assertNotNull("Retrieved villain should not be null", retrievedVillain)
        assertEquals("Should have replacement name", "Replacement", retrievedVillain?.villainName)
        assertEquals("Should have replacement series", "Replacement Series", retrievedVillain?.villainSeries)
    }
}
