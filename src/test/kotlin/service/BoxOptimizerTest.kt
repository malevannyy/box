package service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BoxOptimizerTest {

    @Test
    fun paraboloidalTest() {
        val optimizer = BoxOptimizer(
            n = 2,
            function = { x -> x[0] * x[0] + x[1] * x[1] },
            explicits = listOf(
                UnaryLimit(-10.0, +10.0),
                // no bounds for x1
                Unlimited,
            ),
            implicits = listOf(
                FunctionalLimit(100.0) { x -> x[0] + x[1] },
                FunctionalLimit(50.0) { x -> x[0] },
                FunctionalLimit(50.0) { x -> x[1] },
            )
        )
        val actual = optimizer.solve()

        // verify near (0.0, 0.0)
        assertEquals(0.0, actual[0], 0.1)
        assertEquals(0.0, actual[1], 0.1)
    }
}
