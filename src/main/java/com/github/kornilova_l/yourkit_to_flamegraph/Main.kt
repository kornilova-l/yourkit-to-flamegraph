package com.github.kornilova_l.yourkit_to_flamegraph

import java.io.File

val usage = """
    1. Convert yourkit snapshot:
    java -jar -Dexport.call.tree.cpu -Dexport.csv <path-to-yourkit>/lib/yjp.jar -export ~/Snapshots/my.snapshot <dir-of-converted-file>

    2. Run yourkit-to-flamegraph.jar
    java -jar yourkit-to-flamegraph.jar <path-to-csv-file> <path-to-output-file>
"""

fun main(args: Array<String>) {
    if (args.size != 2) {
        println(usage)
        return
    }
    val csvFile = File(args[0])
    val outFile = File(args[1])

    Converter(csvFile).export(outFile)
}
