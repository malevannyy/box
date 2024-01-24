package service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BoxOptimizerTest {

    @Test
    fun paraboloidalTest() {
        val optimizer = BoxOptimizer(
            function = { x -> x[0] * x[0] + x[1] * x[1] },
            explicits = listOf(),
            implicits = listOf()
        )
        val actual = optimizer.solve()

        // verify near (0.0, 0.0)
        assertEquals(0.0, actual[0], 0.1)
        assertEquals(0.0, actual[1], 0.1)
    }
}
