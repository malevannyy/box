package service

import util.random

typealias Fun<T> = (x: List<T>) -> T
typealias Point = List<Double>

class BoxOptimizer(
    private val n: Int,
    private val function: Fun<Double>,
    // ограничения на ВСЕ переменные, для независимых использовать object : Unbounded
    private val explicits: List<UnaryLimit>,
    // функциональные ограничения
    private val implicits: List<FunctionalLimit>,
) : Solver<Point> {


    // N - complex size
    private val complexSize = when {
        n <= 5 -> 2 * n
        else -> n + 1
    }

    // guard
    init {
        assert(explicits.size == n)
    }

    override fun solve(): Point {

        // build init complex
        val complex = buildInitComplex()




        return listOf(0.0, 0.0)
    }

    // Координаты вершин исходного комплекса Xij
    // @return List<Point>[N]
    private fun buildInitComplex(): List<Point> {
        var complex: Complex
        do {
            val points = (0 until complexSize).map {
                (0 until n).map { i ->
                    explicits[i].low + random() * (explicits[i].high - explicits[i].low)
                }
            }
            complex = Complex(points)
        } while (complex.anyFit())

        complex.improve()

        return listOf()
    }

    // Точка фиксируется как вершина Комплекса, если в ней удовлетворяются все ограничения
    // проверятся выполнение ограничений 2.го рода (ограничения 1-го рода выполняются автоматически).
    private fun Point.fits() = implicits.all { it.fit(this) }

    inner class Complex(
        points: List<Point>
    ) {
        val points = points.toMutableList()
        val fits: MutableList<Boolean> = points.map { it.fits() }.toMutableList()

        init {
            assert(points.size == complexSize)
            assert(points.all { it.size == n })
        }

        fun anyFit() = fits.any { it }

        fun improve() {
            assert(anyFit()) { "all points unfit" }
            // Пусть число точек, удовлетворяющих ограничениям 2-го рода равно Р (Р≥1),
            // тогда (N - P) – число точек, в которых ограничения нарушены.
            for (i in 0 until complexSize) {
                var fit = fits[i]
                if (!fit) {
                    var point = points[i]
                    while (!fit) {
                        point = shift(point)
                        fit = point.fits()
                    }
                }


            }

        }

        private fun center(): Point {
            val array = DoubleArray(n)
            var p = 0
            for (j in 0 until complexSize) {
                if (fits[j]) {
                    p += 1
                    for (i in 0 until n) {
                        array[i] += points[j][i]
                    }
                }
            }
            return array.map { it / p }.toMutableList()
        }

        // операция смещения к центру Р вершин Комплекса,
        // при этом новые координаты точки Xij вычисляются по формуле
        private fun shift(point: Point): Point {
            val center = center()
            return point.mapIndexed { i, x ->
                (x + center[i]) / 2
            }
        }
    }
}
