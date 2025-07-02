package com.developers.contentproviders

import com.developers.contentproviders.data.VillainsDatabaseTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite for all instrumentation tests
 * This allows running all integration tests at once
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    MainActivityInstrumentationTest::class,
    VillainProviderIntegrationTest::class,
    VillainsDatabaseTest::class
)
class InstrumentationTestSuite
