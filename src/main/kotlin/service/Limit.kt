package service

fun interface Limit<T> {
    fun fit(x: T): Boolean
}

open class UnaryLimit(
    val low: Double,
    val high: Double
) : Limit<Double> {
    override fun fit(x: Double) = x in low..high
}

object Unlimited : UnaryLimit(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY) {
    override fun fit(x: Double) = true
}

class FunctionalLimit(
    private val b: Double,
    val f: Fun
) : Limit<DoubleArray> {
    override fun fit(x: DoubleArray) = f(x) <= b
}
