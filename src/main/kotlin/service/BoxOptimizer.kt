package service

import util.indexOfMax
import util.indexOfMin
import util.random
import kotlin.math.abs
import kotlin.math.exp

typealias Fun = (x: DoubleArray) -> Double
typealias Point = DoubleArray

class BoxOptimizer(
    // размерность
    private val n: Int,
    // ЦФ
    private val function: Fun,
    // ограничения на ВСЕ переменные, для независимых использовать object : Unbounded
    private val explicits: List<UnaryLimit>,
    // функциональные ограничения
    private val implicits: List<FunctionalLimit>,
    // точность
    private val epsilon: Double,
    // mirror factor > 1
    private val mirrorFactor: Double = exp(1.0) / 2
) : Solver<Point> {


    // N - complex size
    @Suppress("MagicNumber")
    private val complexSize = when {
        n <= 5 -> 2 * n
        else -> n + 1
    }

    // guard
    init {
        assert(explicits.size == n)
    }

    override fun solve(): Point {

        // 1) Формирование исходного Комплекса
        val complex = buildInitComplex()

        do {
            // 2) Вычисление значений целевой функции Fj для всех N вершин Комплекса:
            val fComplex = complex.map { function(it) }

            // 3) Выбор наилучшего
            val g = fComplex.indexOfMin()
            //  и наихудшего index
            val d = fComplex.indexOfMax()
            // worst function value
            val fd = function(complex[d])

            // 4) Определение координат Сi центра Комплекса с отброшенной наихудшей вершиной
            val ci = complex.center { j -> j != d }

            // 5) Проверка условия окончания поиска. Для этого вычисляется величина В
            val distance = decisionDistance(ci, complex[g], complex[d])
            if (distance < epsilon) {
                return ci
            }

            // 6) Вычисление координаты новой точки Комплекса взамен наихудшей:
            // 7) Проверка выполнения ограничений 2.го рода для новой точки.
            var nova = mirror(ci, complex[d])
            // 8) Вычисление значения целевой функции F0 в новой точке:
            // 9) Нахождение новой вершины смещением xi0 на половину расстояния к лучшей из вершин комплекса с номером G
            var fNova = function(nova)
            while (fNova >= fd) {
                nova = nova.stepTo(complex[g])
                fNova = function(nova)
            }

            // 10) Фиксация точки Х0 и замена на F0 значения FD
            complex[d] = nova
        } while (true)
    }

    // среднее расстояние от центра Комплекса до худшей (D) и лучшей (G) вершин
    private fun decisionDistance(ci: Point, g: Point, d: Point): Double {
        var b = 0.0
        for (i in ci.indices) {
            b += abs(ci[i] - d[i]) + abs(ci[i] - g[i])
        }
        return b / (2 * n)
    }

    // координаты новой точки Комплекса взамен наихудшей:
    // x* = ci - α * (d − ci)
    private fun mirror(ci: Point, d: Point): Point = DoubleArray(n).apply {
        for (i in indices) {
            val x = ci[i] - mirrorFactor * (d[i] - ci[i])
            this[i] = when {
                x < explicits[i].low -> explicits[i].low + epsilon
                x > explicits[i].high -> explicits[i].high - epsilon
                else -> x
            }
        }

        // Проверка выполнения ограничений 2.го рода для новой точки
        // Если ограничение нарушено, то новую точку смещают к центру Комплекса на половину расстояния
        while (!this.fits()) {
            for (i in indices) {
                this[i] = (this[i] + ci[i]) / 2
            }
        }
    }

    private fun Point.stepTo(dest: Point) = apply {
        for (i in indices) {
            this[i] = (this[i] + dest[i]) / 2
        }
    }


    // Координаты вершин исходного комплекса Xij
    // @return List<Point>[N]
    private fun buildInitComplex(): Complex {
        var complex: Complex
        do {
            val points = (0 until complexSize).map {
                (0 until n).map { i ->
                    explicits[i].low + random() * (explicits[i].high - explicits[i].low)
                }.toDoubleArray()
            }
            complex = Complex(points)
        } while (!complex.anyFit())

        if (!complex.allFit()) {
            complex.improve()
        }

        return complex
    }

    // Точка фиксируется как вершина Комплекса, если в ней удовлетворяются все ограничения
    // проверятся выполнение ограничений 2.го рода (ограничения 1-го рода выполняются автоматически).
    private fun Point.fits() = implicits.all { it.fit(this) }

    inner class Complex(
        points: List<Point>
    ) {
        private val points = points.toMutableList()
        private val fits: MutableList<Boolean> = points.map { it.fits() }.toMutableList()

        init {
            assert(points.size == complexSize)
            assert(points.all { it.size == n })
        }

        operator fun get(i: Int) = points[i]
        operator fun set(i: Int, point: Point) {
            points[i] = point
        }

        fun <T> map(f: (p: Point) -> T) = points.map(f)

        fun anyFit() = fits.any { it }
        fun allFit() = fits.all { it }

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

        fun center(predicate: (index: Int) -> Boolean = { j -> fits[j] }): Point {
            val array = DoubleArray(n)
            var p = 0
            for (j in 0 until complexSize) {
                if (predicate(j)) {
                    p += 1
                    for (i in 0 until n) {
                        array[i] += points[j][i]
                    }
                }
            }
            return array.map { it / p }.toDoubleArray()
        }

        // операция смещения к центру Р вершин Комплекса,
        // при этом новые координаты точки Xij вычисляются по формуле
        private fun shift(point: Point): Point {
            val center = center()
            return point.mapIndexed { i, x ->
                (x + center[i]) / 2
            }.toDoubleArray()
        }
    }
}
