# yourkit-to-flamegraph
Yourkit snapshots to flamegraph converter

Tested with yourkit version 2017

## Build jar
```bash
# Linux
./gradlew jar

# Windows
gradlew.bat jar
```
## Usage
1. Convert snapshot to csv file using yourkit:
```bash
java -jar -Dexport.call.tree.cpu -Dexport.csv <path-to-yourkit>/lib/yjp.jar -export ~/Snapshots/my.snapshot <dir-of-converted-file>
```
2. Run yourkit-to-flamegraph.jar
```bash
java -jar yourkit-to-flamegraph.jar <path-to-csv-file> <path-to-output-file>
```