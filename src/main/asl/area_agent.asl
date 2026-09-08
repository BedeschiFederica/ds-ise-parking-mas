// ====================
// BELIEFS AND RULES
// ====================

/*
 * distance(+Position1, +Position2, -Distance)
 * Computes the Manhattan distance between two positions.
 */
distance(position(X1, Y1), position(X2, Y2), math.abs(X1 - X2) + math.abs(Y1 - Y2)).

/*
 * closest_parking(+DriverPosition, +RequiredAvailability, -P, -ParkingPosition)
 * Finds the closest parking P (at position ParkingPosition) to DriverPosition with at least RequiredAvailability
 * available spots.
 */
closest_parking(DriverPosition, RequiredAvailability, P, ParkingPosition) :-
    parking_status(P, ParkingPosition, Availability) &
    Availability >= RequiredAvailability &
    distance(DriverPosition, ParkingPosition, Distance) &
    not (
        parking_status(OtherP, OtherPosition, OtherAvailability) &
        OtherAvailability >= RequiredAvailability &
        distance(DriverPosition, OtherPosition, OtherDistance) &
        OtherDistance < Distance
    ).

// ====================
// PLANS
// ====================

+!start <-
    .my_name(N);
    .findall(P, parking(P), Ps);
    .print("Area agent ", N, " started; contains ", Ps);
    !collect_parking_info(Ps).

+!collect_parking_info([]).

+!collect_parking_info([P | Ps]) <-
    !!request_parking_info(P);
    !collect_parking_info(Ps).

+!request_parking_info(P) <-
    .my_name(N);
    .print("Area ", N, " collects parking info from ", P);
    .send(P, askOne, parking_info(Position, Available), Answer, 3000);
    !process_parking_response(Answer, P).

+!process_parking_response(timeout, P) <-
    .print("Parking agent ", P, " unavailable").

+!process_parking_response(parking_info(Position, Available), P) <-
    .print("Parking agent ", P, " at ", Position, " has ", Available, " available spots");
    +parking_status(P, Position, Available).

+available(A)[source(P)] : parking_status(P, Position, OldA) <-
    -parking_status(P, Position, OldA);
    +parking_status(P, Position, A);
    .print("Area agent updated parking info for ", P, " at ", Position, " to ", A, " available spots").

// ====================
// TEST-GOAL PLANS
// ====================

// available_parking(+DriverPosition, +RequiredAvailability, -Parking, -ParkingPosition)
+?available_parking(DriverPosition, RequiredAvailability, P, ParkingPosition)
        : closest_parking(DriverPosition, RequiredAvailability, P, ParkingPosition) <-
    .print("Closest available parking found: ", P, " at ", ParkingPosition).

// available_parking(+DriverPosition, +RequiredAvailability, -Parking, -ParkingPosition)
+?available_parking(_, _, none, none) : parking_status(_, _, _) <-
    .print("No available parking found").
