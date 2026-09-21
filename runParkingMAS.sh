#!/bin/bash

./gradlew runParkingMain &
MAIN_PID=$!

sleep 2

./gradlew runParkingContainer -Pname=area1 &
AREA1_PID=$!
./gradlew runParkingContainer -Pname=area2 &
AREA2_PID=$!
./gradlew runParkingContainer -Pname=drivers &
DRIVERS_PID=$!

wait "$MAIN_PID"
kill "$AREA1_PID" "$AREA2_PID" "$DRIVERS_PID" 2>/dev/null
wait