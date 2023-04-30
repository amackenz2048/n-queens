#!/bin/bash

QUEENS_JAR="nqueens.jar"
QUEENS_EXE="nqueens"

# Complexity goes up as the factorial of the number of queens, increment judiciously.
MAX_QUEENS=16

echo "Running tests with max queens: ${MAX_QUEENS}"
echo "Running JVM version."
java -jar $QUEENS_JAR $MAX_QUEENS 2>&1 | tail -n 1
echo "Running EXE version."
./$QUEENS_EXE $MAX_QUEENS 2>&1 | tail -n 1

