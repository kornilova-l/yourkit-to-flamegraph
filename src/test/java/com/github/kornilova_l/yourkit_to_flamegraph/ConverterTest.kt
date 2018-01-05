package com.github.kornilova_l.yourkit_to_flamegraph

import org.junit.Test

import org.junit.Assert.*
import java.io.File

class ConverterTest {

    @Test
    fun getStacksSimple() {
        assertEquals(File("src/test/resources/expected/simple.flamegraph").readLines().toSortedSet().joinToString("\n") + "\n",
                stacksToString(Converter(File("src/test/resources/simple.csv")).getStacks()))
    }

    @Test
    fun getStacksDepthDecreases() {
        assertEquals(File("src/test/resources/expected/depth_decreases.flamegraph").readLines().toSortedSet().joinToString("\n") + "\n",
                stacksToString(Converter(File("src/test/resources/depth_decreases.csv")).getStacks()))
    }

    private fun stacksToString(stacks: Map<String, Int>): String {
        val stringBuilder = StringBuilder()
        for ((key, value) in stacks.toSortedMap()) {
            stringBuilder.append(String.format("%s %d%n", key, value))
        }
        return stringBuilder.toString()
    }
}