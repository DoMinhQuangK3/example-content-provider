package com.developers.contentproviders.performance

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.developers.contentproviders.data.Villains
import com.developers.contentproviders.data.VillainsDatabase
import com.developers.contentproviders.data.VillainsDao
import com.developers.contentproviders.util.TestUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import kotlin.system.measureTimeMillis

/**
 * Performance tests for the Content Providers application
 * These tests measure the performance of database operations
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PerformanceTest {

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
    fun closeDb() {
        database.close()
    }

    @Test
    fun testBulkInsertPerformance() = runTest {
        val villains = TestUtils.createLargeTestVillainsList(1000)
        
        val timeMillis = measureTimeMillis {
            villainDao.insertAll(villains)
        }
        
        println("Bulk insert of 1000 villains took: ${timeMillis}ms")
        
        val count = villainDao.count()
        assertEquals("Should have inserted 1000 villains", 1000, count)
        
        // Performance assertion - should complete within 5 seconds
        assertTrue("Bulk insert should complete within 5 seconds", timeMillis < 5000)
    }

    @Test
    fun testIndividualInsertPerformance() = runTest {
        val villains = TestUtils.createLargeTestVillainsList(100)
        
        val timeMillis = measureTimeMillis {
            villains.forEach { villain ->
                villainDao.insert(villain)
            }
        }
        
        println("Individual insert of 100 villains took: ${timeMillis}ms")
        
        val count = villainDao.count()
        assertEquals("Should have inserted 100 villains", 100, count)
        
        // Performance assertion - should complete within 2 seconds
        assertTrue("Individual inserts should complete within 2 seconds", timeMillis < 2000)
    }

    @Test
    fun testQueryAllPerformance() = runTest {
        // Setup: Insert test data
        val villains = TestUtils.createLargeTestVillainsList(1000)
        villainDao.insertAll(villains)
        
        val timeMillis = measureTimeMillis {
            val allVillains = villainDao.getAllVillains()
            assertEquals("Should retrieve 1000 villains", 1000, allVillains.size)
        }
        
        println("Query all 1000 villains took: ${timeMillis}ms")
        
        // Performance assertion - should complete within 1 second
        assertTrue("Query all should complete within 1 second", timeMillis < 1000)
    }

    @Test
    fun testQueryByIdPerformance() = runTest {
        // Setup: Insert test data
        val villains = TestUtils.createLargeTestVillainsList(1000)
        villainDao.insertAll(villains)
        
        val timeMillis = measureTimeMillis {
            repeat(100) { index ->
                val villain = villainDao.getVillainById((index + 1).toLong())
                assertNotNull("Villain should exist", villain)
            }
        }
        
        println("100 individual queries by ID took: ${timeMillis}ms")
        
        // Performance assertion - should complete within 1 second
        assertTrue("Individual queries should complete within 1 second", timeMillis < 1000)
    }

    @Test
    fun testUpdatePerformance() = runTest {
        // Setup: Insert test data
        val villains = TestUtils.createLargeTestVillainsList(100)
        villainDao.insertAll(villains)
        
        val updatedVillains = villains.map { villain ->
            villain.copy(villainName = "Updated ${villain.villainName}")
        }
        
        val timeMillis = measureTimeMillis {
            updatedVillains.forEach { villain ->
                villainDao.update(villain)
            }
        }
        
        println("100 individual updates took: ${timeMillis}ms")
        
        // Verify updates
        val firstUpdatedVillain = villainDao.getVillainById(1)
        assertTrue("Villain name should be updated", 
                  firstUpdatedVillain?.villainName?.startsWith("Updated") == true)
        
        // Performance assertion - should complete within 2 seconds
        assertTrue("Updates should complete within 2 seconds", timeMillis < 2000)
    }

    @Test
    fun testDeletePerformance() = runTest {
        // Setup: Insert test data
        val villains = TestUtils.createLargeTestVillainsList(100)
        villainDao.insertAll(villains)
        
        val timeMillis = measureTimeMillis {
            repeat(100) { index ->
                villainDao.deleteById((index + 1).toLong())
            }
        }
        
        println("100 individual deletes took: ${timeMillis}ms")
        
        val finalCount = villainDao.count()
        assertEquals("Should have deleted all villains", 0, finalCount)
        
        // Performance assertion - should complete within 2 seconds
        assertTrue("Deletes should complete within 2 seconds", timeMillis < 2000)
    }

    @Test
    fun testMemoryUsage() = runTest {
        val runtime = Runtime.getRuntime()
        
        // Measure memory before
        val memoryBefore = runtime.totalMemory() - runtime.freeMemory()
        
        // Insert a large amount of data
        val villains = TestUtils.createLargeTestVillainsList(5000)
        villainDao.insertAll(villains)
        
        // Force garbage collection
        System.gc()
        Thread.sleep(100)
        
        // Measure memory after
        val memoryAfter = runtime.totalMemory() - runtime.freeMemory()
        
        val memoryUsed = memoryAfter - memoryBefore
        
        println("Memory used for 5000 villains: ${memoryUsed / 1024}KB")
        
        // Memory usage should be reasonable (less than 50MB)
        assertTrue("Memory usage should be reasonable", memoryUsed < 50 * 1024 * 1024)
    }

    @Test
    fun testConcurrentOperations() = runTest {
        val villains = TestUtils.createLargeTestVillainsList(100)
        
        val timeMillis = measureTimeMillis {
            // Simulate concurrent operations
            villainDao.insertAll(villains.take(50))
            villainDao.insertAll(villains.drop(50))
            
            val count = villainDao.count()
            assertEquals("Should have 100 villains", 100, count)
            
            // Update some records
            val updateVillain = villains.first().copy(villainName = "Concurrent Update")
            villainDao.update(updateVillain)
            
            // Delete some records
            villainDao.deleteById(1)
            villainDao.deleteById(2)
            
            val finalCount = villainDao.count()
            assertEquals("Should have 98 villains after deletions", 98, finalCount)
        }
        
        println("Concurrent operations took: ${timeMillis}ms")
        
        // Performance assertion - should complete within 3 seconds
        assertTrue("Concurrent operations should complete within 3 seconds", timeMillis < 3000)
    }
}
