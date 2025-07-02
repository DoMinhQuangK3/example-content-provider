package com.developers.contentproviders.util

import com.developers.contentproviders.data.Villains

/**
 * Test utility class containing helper methods and test data
 */
object TestUtils {

    /**
     * Creates test villains for testing purposes
     */
    fun createTestVillains(): List<Villains> {
        return listOf(
            Villains("Test Joker", "Test Batman", 1),
            Villains("Test Lex Luthor", "Test Superman", 2),
            Villains("Test Reverse Flash", "Test Flash", 3),
            Villains("Test Harley Quinn", "Test Suicide Squad", 4),
            Villains("Test Deathstroke", "Test Arrow", 5)
        )
    }

    /**
     * Creates a single test villain
     */
    fun createTestVillain(): Villains {
        return Villains("Test Villain", "Test Series", 1)
    }

    /**
     * Creates test villains with custom names
     */
    fun createTestVillainsWithNames(names: List<String>): List<Villains> {
        return names.mapIndexed { index, name ->
            Villains(name, "Test Series ${index + 1}", (index + 1).toLong())
        }
    }

    /**
     * Creates a large list of test villains for performance testing
     */
    fun createLargeTestVillainsList(count: Int): List<Villains> {
        return (1..count).map { index ->
            Villains(
                villainName = "Test Villain $index",
                villainSeries = "Test Series $index",
                id = index.toLong()
            )
        }
    }

    /**
     * Common villain names for testing
     */
    val TEST_VILLAIN_NAMES = listOf(
        "Test Joker",
        "Test Lex Luthor", 
        "Test Magneto",
        "Test Loki",
        "Test Thanos",
        "Test Green Goblin",
        "Test Doctor Doom",
        "Test Catwoman",
        "Test Penguin",
        "Test Riddler"
    )

    /**
     * Common series names for testing
     */
    val TEST_SERIES_NAMES = listOf(
        "Test Batman",
        "Test Superman",
        "Test X-Men",
        "Test Thor",
        "Test Avengers",
        "Test Spider-Man",
        "Test Fantastic Four"
    )

    /**
     * Validates that a villain has valid data
     */
    fun isValidVillain(villain: Villains): Boolean {
        return villain.villainName.isNotBlank() && 
               villain.villainSeries.isNotBlank() && 
               villain.id >= 0
    }

    /**
     * Creates a villain with invalid data for negative testing
     */
    fun createInvalidVillain(): Villains {
        return Villains("", "", -1)
    }

    /**
     * Compares two villain lists ignoring order
     */
    fun villainsListEquals(list1: List<Villains>, list2: List<Villains>): Boolean {
        if (list1.size != list2.size) return false
        
        val sortedList1 = list1.sortedBy { it.id }
        val sortedList2 = list2.sortedBy { it.id }
        
        return sortedList1 == sortedList2
    }

    /**
     * Gets expected sample villain names from the data class
     */
    fun getExpectedSampleVillainNames(): List<String> {
        return Villains.SAMPLE_VILLAINS.map { it.first }
    }

    /**
     * Gets expected sample series names from the data class
     */
    fun getExpectedSampleSeriesNames(): List<String> {
        return Villains.SAMPLE_VILLAINS.map { it.second }
    }
}
