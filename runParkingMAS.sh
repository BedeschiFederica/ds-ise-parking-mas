./gradlew runParkingMain &
sleep 2

./gradlew runParkingContainer -Pname=area1 &
./gradlew runParkingContainer -Pname=area2 &
./gradlew runParkingContainer -Pname=drivers &

wait