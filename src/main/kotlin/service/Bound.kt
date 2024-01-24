package service

fun interface Bound<T> {
    fun pass(x: T): Boolean
}

data class UnaryBound(
    val low: Double,
    val high: Double
) : Bound<Double> {
    override fun pass(x: Double) = x in low..high
}

data class FunBound(
    val b: Double,
    val f: Fun<Double>
) : Bound<List<Double>> {
    override fun pass(x: List<Double>) = f(x) <= b
}
