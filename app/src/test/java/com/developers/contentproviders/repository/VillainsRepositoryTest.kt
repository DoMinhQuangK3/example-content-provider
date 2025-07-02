package com.developers.contentproviders.repository

import com.developers.contentproviders.data.Villains
import com.developers.contentproviders.data.VillainsDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

/**
 * Unit tests for VillainsRepository
 */
@ExperimentalCoroutinesApi
class VillainsRepositoryTest {

    @Mock
    private lateinit var mockDao: VillainsDao

    private lateinit var repository: VillainsRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = VillainsRepository(mockDao)
    }

    @Test
    fun `getAllVillains returns flow from dao`() = runTest {
        val testVillains = listOf(
            Villains("Joker", "Batman", 1),
            Villains("Lex Luthor", "Superman", 2)
        )
        
        whenever(mockDao.getAllVillainsFlow()).thenReturn(flowOf(testVillains))
        
        val result = repository.allVillains.first()
        
        assertEquals(testVillains, result)
        verify(mockDao).getAllVillainsFlow()
    }

    @Test
    fun `insert villain calls dao insert`() = runTest {
        val testVillain = Villains("Test Villain", "Test Series", 1)
        val expectedId = 123L
        
        whenever(mockDao.insert(testVillain)).thenReturn(expectedId)
        
        val result = repository.insert(testVillain)
        
        assertEquals(expectedId, result)
        verify(mockDao).insert(testVillain)
    }

    @Test
    fun `update villain calls dao update`() = runTest {
        val testVillain = Villains("Updated Villain", "Updated Series", 1)
        val expectedCount = 1
        
        whenever(mockDao.update(testVillain)).thenReturn(expectedCount)
        
        val result = repository.update(testVillain)
        
        assertEquals(expectedCount, result)
        verify(mockDao).update(testVillain)
    }

    @Test
    fun `delete villain calls dao deleteById`() = runTest {
        val villainId = 1L
        val expectedCount = 1
        
        whenever(mockDao.deleteById(villainId)).thenReturn(expectedCount)
        
        val result = repository.delete(villainId)
        
        assertEquals(expectedCount, result)
        verify(mockDao).deleteById(villainId)
    }

    @Test
    fun `getAllVillains calls dao getAllVillains`() = runTest {
        val testVillains = listOf(
            Villains("Joker", "Batman", 1),
            Villains("Lex Luthor", "Superman", 2)
        )
        
        whenever(mockDao.getAllVillains()).thenReturn(testVillains)
        
        val result = repository.getAllVillains()
        
        assertEquals(testVillains, result)
        verify(mockDao).getAllVillains()
    }

    @Test
    fun `getVillainById calls dao getVillainById`() = runTest {
        val villainId = 1L
        val testVillain = Villains("Joker", "Batman", villainId)
        
        whenever(mockDao.getVillainById(villainId)).thenReturn(testVillain)
        
        val result = repository.getVillainById(villainId)
        
        assertEquals(testVillain, result)
        verify(mockDao).getVillainById(villainId)
    }

    @Test
    fun `getVillainById returns null when villain not found`() = runTest {
        val villainId = 999L
        
        whenever(mockDao.getVillainById(villainId)).thenReturn(null)
        
        val result = repository.getVillainById(villainId)
        
        assertNull(result)
        verify(mockDao).getVillainById(villainId)
    }

    @Test
    fun `getVillainsCount calls dao count`() = runTest {
        val expectedCount = 5
        
        whenever(mockDao.count()).thenReturn(expectedCount)
        
        val result = repository.getVillainsCount()
        
        assertEquals(expectedCount, result)
        verify(mockDao).count()
    }
}
