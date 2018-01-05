package com.github.kornilova_l.yourkit_to_flamegraph

import java.io.File
import java.io.FileOutputStream


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
        file.forEachLine { line ->
            val values = line.split("\",\"")
            if (values[0].contains('(')) {
                val time = getTime(values)
                val depth = getDepth(values) - 2
                val name = getName(values)
                if (depth < currentStack.size) {
                    for (i in currentStack.size - 1 downTo depth) {
                        currentStack.removeAt(i)
                    }
                }
                if (currentStack.isNotEmpty()) {
                    decreaseTime(currentStack, time)
                }
                currentStack.add(name)
                addStacktrace(currentStack, time)
            }
        }
    }

    private fun addStacktrace(currentStack: MutableList<String>, time: Int) {
        val stacktrace = currentStack.joinToString(";")
        if (stacks.contains(stacktrace)) {
            val previousTime = stacks[stacktrace]!!
            stacks[stacktrace] = previousTime + time
        } else {
            stacks[stacktrace] = time
        }
    }

    private fun decreaseTime(currentStack: MutableList<String>, time: Int) {
        val stacktrace = currentStack.joinToString(";")
        val previousTime = stacks[stacktrace]!!
        if (previousTime - time < 0) {
            throw AssertionError("Time of method is negative")
        }
        if (previousTime - time == 0) {
            stacks.remove(stacktrace)
        } else {
            stacks[stacktrace] = previousTime - time
        }
    }

    private fun getName(values: List<String>): String {
        val name = values[0].removePrefix("\"")
        val openBracketPos = name.lastIndexOf('(')
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

    fun export(outFile: File) {
        FileOutputStream(outFile).use { outputStream ->
            for (entry in stacks.entries) {
                outputStream.write("${entry.key} ${entry.value}\n".toByteArray())
            }
        }
    }
}