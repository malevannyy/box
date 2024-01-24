package util

import kotlin.random.Random

private val random = Random(System.currentTimeMillis())

fun random(): Double {
    return when (val r = random.nextDouble()) {
        0.0 -> random()
        else -> r
    }
}
