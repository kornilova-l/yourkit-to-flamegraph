package com.github.kornilova_l.yourkit_to_flamegraph

import java.io.File


/**
 * Converts csv file that was generated from yourkit snapshot
 * to map<String, Int> where key is stacktrace and value is
 * how many this stacktrace was present during execution.
 */
class Converter(file: File) {
    private val stacks = HashMap<String, Int>()

    /**
     * If new stack has bigger depth:
     * - save previous stack with time = previous time - current time
     * If new stack has smaller or the same depth:
     * - save previous stack with time = previous time
     */
    init {
        if (!file.name.endsWith("csv")) {
            throw IllegalArgumentException("Not a csv file")
        }
        val currentStack = mutableListOf<String>()
        var previousTime = 0
        file.forEachLine { line ->
            val values = line.split("\",\"")
            if (values[0].contains('(')) {
                val time = getTime(values)
                val depth = getDepth(values) - 2

                updateStacks(currentStack, time, depth, previousTime)

                if (values[0].contains('(')) { // if contains method
                    updateCurrentStack(currentStack, depth, values)
                }

                previousTime = time
            }
        }
        stacks[currentStack.joinToString(";")] = previousTime
    }

    private fun updateCurrentStack(currentStack: MutableList<String>, depth: Int, values: List<String>) {
        val name = getName(values)
        if (depth < currentStack.size) {
            for (i in currentStack.size - 1 downTo depth) {
                currentStack.removeAt(i)
            }
        }
        currentStack.add(name)
    }

    private fun updateStacks(currentStack: List<String>, time: Int, depth: Int, previousTime: Int) {
        if (depth >= currentStack.size) { // if bigger (depth starts with 0)
            if (currentStack.isNotEmpty() && previousTime - time != 0) {
                stacks[currentStack.joinToString(";")] = previousTime - time
            }
        } else {
            if (currentStack.isNotEmpty()) {
                stacks[currentStack.joinToString(";")] = previousTime
            }
        }
    }

    private fun getName(values: List<String>): String {
        val name = values[0].removePrefix("\"")
        val openBracketPos = name.indexOf('(')
        val lastSpacePos = name.substring(0, openBracketPos).lastIndexOf(' ') // remove parameters because they may contain spaces
        return name.substring(lastSpacePos + 1, name.length)
    }

    private fun getDepth(values: List<String>): Int {
        return Integer.parseInt(values[2].removeSuffix("\""))
    }

    private fun getTime(values: List<String>): Int {
        return Integer.parseInt(values[1])
    }

    fun getStacks(): Map<String, Int> {
        return stacks
    }
}