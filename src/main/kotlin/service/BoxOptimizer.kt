package service

typealias Fun<T> = (x: List<T>) -> T

class BoxOptimizer(
    private val function: Fun<Double>,
    // explicits
    private val explicits: List<UnaryBound>,
    // implicits
    private val implicits: List<FunBound>,
) {
    fun solve(): List<Double> {


        return listOf(0.0, 0.0)
    }
}
