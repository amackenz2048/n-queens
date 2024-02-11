#!/bin/bash

QUEENS_JAR="nqueens.jar"
QUEENS_EXE="nqueens"

# Complexity goes up as the factorial of the number of queens, increment judiciously.
# 15 queens is solved on the JVM in ~2 seconds on a 12th Gen Core i7-12700H with 14 cores (20 threads).
MAX_QUEENS=16

echo "Running tests with max queens: ${MAX_QUEENS}"
echo "Running JVM version."
JVM_OUTPUT=$(java -jar $QUEENS_JAR $MAX_QUEENS 2>&1 | tail -n 1)
echo "Running EXE version."
EXE_OUTPUT=$(./$QUEENS_EXE $MAX_QUEENS 2>&1 | tail -n 1)

JVM_TIME_S=$(echo $JVM_OUTPUT | sed 's/Solved in //' | sed 's/s//')
EXE_TIME_S=$(echo $EXE_OUTPUT | sed 's/Solved in //' | sed 's/s//')

echo "------------------------------"
echo "JVM time to solve: ${JVM_TIME_S} seconds"
echo "EXE time to solve: ${EXE_TIME_S} seconds"
DIFF=$(bc -l << __EOF
scale=2
$EXE_TIME_S / $JVM_TIME_S
__EOF
)
echo " => JVM faster by: %$DIFF "
