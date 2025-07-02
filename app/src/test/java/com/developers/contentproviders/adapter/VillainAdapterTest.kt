package com.developers.contentproviders.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.developers.contentproviders.data.Villains
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

/**
 * Unit tests for VillainAdapter
 */
@RunWith(AndroidJUnit4::class)
class VillainAdapterTest {

    @Mock
    private lateinit var mockOnItemClick: (Villains) -> Unit

    private lateinit var adapter: VillainAdapter

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        adapter = VillainAdapter(mockOnItemClick)
    }

    @Test
    fun `adapter initially has empty list`() {
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun `submitList updates item count`() {
        val villains = listOf(
            Villains("Joker", "Batman", 1),
            Villains("Lex Luthor", "Superman", 2)
        )

        adapter.submitList(villains)

        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun `getItemId returns correct position`() {
        val villains = listOf(
            Villains("Joker", "Batman", 1),
            Villains("Lex Luthor", "Superman", 2)
        )

        adapter.submitList(villains)

        assertEquals(0, adapter.getItemId(0))
        assertEquals(1, adapter.getItemId(1))
    }

    @Test
    fun `diffCallback areItemsTheSame compares by id`() {
        val callback = VillainAdapter.VillainsDiffCallback()
        val villain1 = Villains("Joker", "Batman", 1)
        val villain2 = Villains("Different Name", "Different Series", 1)
        val villain3 = Villains("Joker", "Batman", 2)

        assertTrue("Same ID should return true", callback.areItemsTheSame(villain1, villain2))
        assertFalse("Different ID should return false", callback.areItemsTheSame(villain1, villain3))
    }

    @Test
    fun `diffCallback areContentsTheSame compares full objects`() {
        val callback = VillainAdapter.VillainsDiffCallback()
        val villain1 = Villains("Joker", "Batman", 1)
        val villain2 = Villains("Joker", "Batman", 1)
        val villain3 = Villains("Lex Luthor", "Superman", 1)

        assertTrue("Identical villains should return true", callback.areContentsTheSame(villain1, villain2))
        assertFalse("Different content should return false", callback.areContentsTheSame(villain1, villain3))
    }

    @Test
    fun `currentList returns submitted list`() {
        val villains = listOf(
            Villains("Joker", "Batman", 1),
            Villains("Lex Luthor", "Superman", 2)
        )

        adapter.submitList(villains)

        assertEquals(villains, adapter.currentList)
    }

    @Test
    fun `submitList with null clears the list`() {
        val villains = listOf(
            Villains("Joker", "Batman", 1),
            Villains("Lex Luthor", "Superman", 2)
        )

        adapter.submitList(villains)
        assertEquals(2, adapter.itemCount)

        adapter.submitList(null)
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun `submitList with empty list`() {
        val villains = listOf(
            Villains("Joker", "Batman", 1),
            Villains("Lex Luthor", "Superman", 2)
        )

        adapter.submitList(villains)
        assertEquals(2, adapter.itemCount)

        adapter.submitList(emptyList())
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun `getItem returns correct villain at position`() {
        val villains = listOf(
            Villains("Joker", "Batman", 1),
            Villains("Lex Luthor", "Superman", 2)
        )

        adapter.submitList(villains)

        assertEquals(villains[0], adapter.currentList[0])
        assertEquals(villains[1], adapter.currentList[1])
    }

    @Test
    fun `adapter handles large lists efficiently`() {
        val largeList = (1..1000).map { index ->
            Villains("Villain $index", "Series $index", index.toLong())
        }

        adapter.submitList(largeList)

        assertEquals(1000, adapter.itemCount)
        assertEquals("Villain 1", adapter.currentList[0].villainName)
        assertEquals("Villain 1000", adapter.currentList[999].villainName)
    }

    @Test
    fun `diffCallback handles duplicate content correctly`() {
        val callback = VillainAdapter.VillainsDiffCallback()
        val villain1 = Villains("Same Name", "Same Series", 1)
        val villain2 = Villains("Same Name", "Same Series", 2) // Different ID

        assertFalse("Different IDs should not be same items", callback.areItemsTheSame(villain1, villain2))
        assertFalse("Different objects should not have same content", callback.areContentsTheSame(villain1, villain2))
    }
}
