package com.developers.contentproviders.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.developers.contentproviders.data.Villains
import com.developers.contentproviders.data.VillainsDao
import com.developers.contentproviders.data.VillainsDatabase
import com.developers.contentproviders.repository.VillainsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Unit tests for VillainsViewModel
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class VillainsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockDao: VillainsDao

    @Mock
    private lateinit var mockDatabase: VillainsDatabase

    @Mock
    private lateinit var mockRepository: VillainsRepository

    @Mock
    private lateinit var observer: Observer<List<Villains>>

    private lateinit var application: Application
    private lateinit var viewModel: VillainsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        application = RuntimeEnvironment.getApplication()
    }

    @Test
    fun `viewModel initialization creates repository and livedata`() = runTest {
        // Mock the static method call
        val mockStatic: MockedStatic<VillainsDatabase> = mockStatic(VillainsDatabase::class.java)
        
        try {
            val testVillains = listOf(
                Villains("Joker", "Batman", 1),
                Villains("Lex Luthor", "Superman", 2)
            )

            whenever(mockDatabase.villainDao()).thenReturn(mockDao)
            whenever(mockRepository.allVillains).thenReturn(flowOf(testVillains))
            
            mockStatic.`when`<VillainsDatabase> { 
                VillainsDatabase.getDatabase(any(), any()) 
            }.thenReturn(mockDatabase)

            viewModel = VillainsViewModel(application)
            
            // Verify that the database was accessed
            mockStatic.verify { VillainsDatabase.getDatabase(any(), any()) }
            
        } finally {
            mockStatic.close()
        }
    }

    @Test
    fun `insert villain calls repository insert`() = runTest {
        val mockStatic: MockedStatic<VillainsDatabase> = mockStatic(VillainsDatabase::class.java)
        
        try {
            whenever(mockDatabase.villainDao()).thenReturn(mockDao)
            whenever(mockRepository.allVillains).thenReturn(flowOf(emptyList()))
            
            mockStatic.`when`<VillainsDatabase> { 
                VillainsDatabase.getDatabase(any(), any()) 
            }.thenReturn(mockDatabase)

            viewModel = VillainsViewModel(application)
            
            val testVillain = Villains("Test Villain", "Test Series", 1)
            viewModel.insert(testVillain)
            
            // Give coroutine time to execute
            Thread.sleep(100)
            
            // Verify repository insert was called
            verify(mockRepository).insert(testVillain)
            
        } finally {
            mockStatic.close()
        }
    }

    @Test
    fun `update villain calls repository update`() = runTest {
        val mockStatic: MockedStatic<VillainsDatabase> = mockStatic(VillainsDatabase::class.java)
        
        try {
            whenever(mockDatabase.villainDao()).thenReturn(mockDao)
            whenever(mockRepository.allVillains).thenReturn(flowOf(emptyList()))
            
            mockStatic.`when`<VillainsDatabase> { 
                VillainsDatabase.getDatabase(any(), any()) 
            }.thenReturn(mockDatabase)

            viewModel = VillainsViewModel(application)
            
            val testVillain = Villains("Updated Villain", "Updated Series", 1)
            viewModel.update(testVillain)
            
            // Give coroutine time to execute
            Thread.sleep(100)
            
            // Verify repository update was called
            verify(mockRepository).update(testVillain)
            
        } finally {
            mockStatic.close()
        }
    }

    @Test
    fun `delete villain calls repository delete`() = runTest {
        val mockStatic: MockedStatic<VillainsDatabase> = mockStatic(VillainsDatabase::class.java)
        
        try {
            whenever(mockDatabase.villainDao()).thenReturn(mockDao)
            whenever(mockRepository.allVillains).thenReturn(flowOf(emptyList()))
            
            mockStatic.`when`<VillainsDatabase> { 
                VillainsDatabase.getDatabase(any(), any()) 
            }.thenReturn(mockDatabase)

            viewModel = VillainsViewModel(application)
            
            val villainId = 1L
            viewModel.delete(villainId)
            
            // Give coroutine time to execute
            Thread.sleep(100)
            
            // Verify repository delete was called
            verify(mockRepository).delete(villainId)
            
        } finally {
            mockStatic.close()
        }
    }
}
