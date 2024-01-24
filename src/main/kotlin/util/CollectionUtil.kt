package util

fun Collection<Double>.indexOfMin(): Int {
    var min = Double.POSITIVE_INFINITY
    var index = -1
    forEachIndexed { i, e ->
        if (e < min) {
            min = e
            index = i
        }
    }

    assert(index in indices)
    return index
}

fun Collection<Double>.indexOfMax(): Int {
    var max = Double.NEGATIVE_INFINITY
    var index = -1
    forEachIndexed { i, e ->
        if (e > max) {
            max = e
            index = i
        }
    }

    assert(index in indices)
    return index
}

