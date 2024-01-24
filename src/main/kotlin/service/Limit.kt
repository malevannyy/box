package service

fun interface Limit<T> {
    fun pass(x: T): Boolean
}

open class UnaryLimit(
    val low: Double,
    val high: Double
) : Limit<Double> {
    override fun pass(x: Double) = x in low..high
}

object Unlimited : UnaryLimit(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY) {
    override fun pass(x: Double) = true
}

class FunctionalLimit(
    private val b: Double,
    val f: Fun<Double>
) : Limit<List<Double>> {
    override fun pass(x: List<Double>) = f(x) <= b
}
