# EDI: A Program to Play the Game of Amazons

>Note: This Java implementation of EDI is no longer being maintained. The version being actively developed is available [here](https://github.com/Chad-Glazier/edi).

This project is meant to implement a simple AI to play the [Game of the Amazons](https://en.wikipedia.org/wiki/Game_of_the_Amazons). A draft of the original written report for EDI can be found [here](./EDI_Report.pdf).

The `javadoc`-generated documentation for this implementation can be found [here](https://chad-glazier.github.io/edi_java/).

## Running the Program

At the top level, you can see three scripts:
- [run_benchmark.ps1](./run_benchmark.ps1) will run the benchmarks for the project (from the [benchmarking module](./benchmarking/)).
- [run_unit_tests.ps1](./run_unit_tests.ps1) will just run the unit tests for the core project (from [here](./core/src/test/java/com/chadglazier/)).
- [run.ps1](./run.ps1) will run the main program, which can be used for playing against other opponents on the game server.

These scripts are written for PowerShell, but they are really just Maven commands and they can be run in other shells if you reformat them slightly.

## Dependencies 

This project depends on the following packages:
- [Junit](https://docs.junit.org/5.10.5/user-guide/) is used for creating unit tests.
- [Java Microbenchmark Harness (JMH)](https://github.com/openjdk/jmh/tree/master) is used in the [benchmarking module](./benchmarking/) to compare the performance of different methods/implementations.

Not listed here is the package used to communicate with the game server used for a tournament at UBC-O, which was written by Dr. Yong Gao.
