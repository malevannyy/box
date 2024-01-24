package service

import kotlin.random.Random

typealias Fun<T> = (x: List<T>) -> T

@Suppress("PrivatePropertyName")
class BoxOptimizer(
    private val n: Int,
    private val function: Fun<Double>,
    // ограничения на ВСЕ переменные, для независимых использовать object : Unbounded
    private val explicits: List<UnaryLimit>,
    // функциональные ограничения
    private val implicits: List<FunctionalLimit>,
) : Solver<List<Double>> {

    private val random = Random(System.currentTimeMillis())

    // complex size
    private val N = when {
        n <= 5 -> 2 * n
        else -> n + 1
    }

    // guard
    init {
        assert(explicits.size == n)
    }

    override fun solve(): List<Double> {

        // build init complex
        val complex = buildInitComplex()




        return listOf(0.0, 0.0)
    }

    private fun buildInitComplex(): List<Double> {
        var init: List<Double>
        var compliance: List<Boolean>

        // init with random values
        do {
            init = (0 until n).map {
                explicits[it].low + random.next() * (explicits[it].high - explicits[it].low)
            }
            compliance = init.comply()
        } while (compliance.any { it })

        // Пусть число точек, удовлетворяющих ограничениям 2-го рода равно Р (Р≥1),
        // тогда (N - P) – число точек, в которых ограничения нарушены.




        return init
    }

    private fun List<Double>.comply() = mapIndexed { i, x ->
        explicits[i].pass(x) && implicits.all { it.pass(this) }
    }

    private fun Random.next() = nextDouble()
}

