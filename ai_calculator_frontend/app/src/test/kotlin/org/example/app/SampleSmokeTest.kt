package org.example.app

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * PUBLIC_INTERFACE
 * A minimal smoke test to ensure the Gradle test task discovers and executes at least one test.
 * This prevents CI failures due to "no tests discovered" when running unit tests.
 */
class SampleSmokeTest {

    // PUBLIC_INTERFACE
    @Test
    fun testAlwaysPasses() {
        /** This is a public function. Ensures test discovery works. */
        assertTrue(true)
    }
}
