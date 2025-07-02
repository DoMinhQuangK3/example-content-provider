package com.developers.contentproviders

import com.developers.contentproviders.adapter.VillainAdapterTest
import com.developers.contentproviders.data.VillainsTest
import com.developers.contentproviders.repository.VillainsRepositoryTest
import com.developers.contentproviders.viewmodel.VillainsViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite for all unit tests
 * This allows running all unit tests at once
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    VillainsTest::class,
    VillainsRepositoryTest::class,
    VillainsViewModelTest::class,
    VillainAdapterTest::class,
    VillainProviderTest::class
)
class UnitTestSuite
