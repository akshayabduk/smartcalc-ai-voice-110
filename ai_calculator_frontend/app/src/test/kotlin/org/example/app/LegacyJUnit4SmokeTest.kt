package org.example.app

import org.junit.Test
import org.junit.Assert.assertTrue

/**
 * PUBLIC_INTERFACE
 * Legacy JUnit 4 smoke test to ensure that at least one test is discovered and executed
 * by environments defaulting to the JUnit 4 runner.
 */
class LegacyJUnit4SmokeTest {

    // PUBLIC_INTERFACE
    @Test
    fun alwaysPasses() {
        /** This is a public function. Ensures JUnit 4 test discovery works. */
        assertTrue(true)
    }
}
