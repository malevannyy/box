package service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BoxOptimizerTest {

    private val optimizer = BoxOptimizer()

    @Test
    fun test1() {
        val actual = optimizer.optimize(null)

        assertEquals(null, actual)
    }
}
