// ====================
// BELIEFS AND RULES
// ====================

/*
 * adjacent(+Position1, +Position2)
 * Checks if two positions are adjacent (i.e., one step apart in any direction).
 */
adjacent(position(X1, Y1), position(X2, Y2)) :-
    (X1 == X2 & (Y1 == Y2 + 1 | Y1 == Y2 - 1)) | (Y1 == Y2 & (X1 == X2 + 1 | X1 == X2 - 1)).

/*
 * arrived(+ArrivalCondition, +DriverPosition, +DestinationPosition)
 * Checks if a driver has arrived at a destination based on the arrival condition.
 */
arrived(adjacent, position(X, Y), position(Xd, Yd)) :- adjacent(position(X, Y), position(Xd, Yd)).
arrived(exact, position(X, Y), position(Xd, Yd)) :- X == Xd & Y == Yd.

/*
 * directions(+FromPosition, +ToPosition, -PrimaryDirection, -SecondaryDirection)
 * Determines the directions to move from one position to another.
 */
directions(position(FromX, FromY), position(ToX, ToY), north, east) :- FromX > ToX & FromY < ToY.
directions(position(FromX, FromY), position(ToX, ToY), north, west) :- FromX > ToX.
directions(position(FromX, FromY), position(ToX, ToY), south, east) :- FromX < ToX & FromY < ToY.
directions(position(FromX, FromY), position(ToX, ToY), south, west) :- FromX < ToX.
directions(position(FromX, FromY), position(ToX, ToY), east, north) :- FromY < ToY.
directions(position(FromX, FromY), position(ToX, ToY), west, north).

/*
 * opposite(+Direction, -OppositeDirection)
 * Determines the opposite direction of a given direction.
 */
opposite(north, south).
opposite(south, north).
opposite(east, west).
opposite(west, east).

/*
 * preferred_directions(+FromPosition, +ToPosition, -Directions)
 * Determines the preferred directions to move from one position to another.
 */
preferred_directions(FromPosition, ToPosition, [Primary, Secondary, SecondaryOpposite, PrimaryOpposite]) :-
    directions(FromPosition, ToPosition, Primary, Secondary) &
    opposite(Secondary, SecondaryOpposite) &
    opposite(Primary, PrimaryOpposite).

/*
 * random_between(+X, +Y, -RandomInt)
 * Generates a random integer between X and Y (inclusive).
 */
random_between(X, Y, math.floor(X) + math.floor(R * (Y - X + 1))) :- .random(R).

// ====================
// PERCEPTS
// ====================

+position(X, Y) : not home(_) <-
    .print("Driver is at home at (", X, ", ", Y, ")");
    +home(position(X, Y)).

+position(X, Y) <-
    .print("Driver at (", X, ", ", Y, ")").

+current_area(A) <-
    .print("Driver is in area ", A).

+nearest_area(A) <-
    .my_name(N);
    .print("Driver ", N, " nearest area: ", A).

// ====================
// PLANS
// ====================

+!start <-
    .my_name(N);
    .print("Driver agent ", N, " started");
    !request_parking.

// ========== Request parking to Area Agent of current area ==========
// Protocol message: available_parking(+DriverPosition, +RequiredAvailability, -Parking, -ParkingPosition)

+!request_parking : current_area(A) & position(X, Y) & required_availability(R) <-
    .my_name(N);
    .print("Driver ", N, " requesting parking to area ", A);
    .send(A, askOne, available_parking(position(X, Y), R, P, Position), Answer, 3000);
    !process_parking_response(Answer).

+!process_parking_response(available_parking(_, _, none, _)) <-
    .print("No available parking found; contacting nearest area agent");
    !request_parking_to_nearest_area_agent.

+!process_parking_response(available_parking(_, _, P, Position)) <-
    .print("Going to parking lot ", P, " at ", Position);
    !go_to_parking_lot(P, Position).

+!process_parking_response(timeout) <- // TODO to improve
    .print("Area agent unavailable; retrying in 0.5s");
    .wait(500);
    !request_parking.

+!process_parking_response(Error) <- // TODO to improve
    .print("Error occurred while contacting area agent: ", Error);
    .wait(500);
    !request_parking.

// ========== Request parking to Area Agent of nearest area ==========
// Protocol message: available_parking(+DriverPosition, +RequiredAvailability, -Parking, -ParkingPosition)

+!request_parking_to_nearest_area_agent : nearest_area(A) & position(X, Y) & required_availability(R) <-
    .my_name(N);
    .print("Driver ", N, " requesting parking to area ", A);
    .send(A, askOne, available_parking(position(X, Y), R, P, Position), Answer, 3000);
    !process_parking_response_from_nearest_area_agent(Answer).

+!request_parking_to_nearest_area_agent <-
    .print("No nearest area; retrying in 0.5s to area agent of current area");
    .wait(500);
    !request_parking.

+!process_parking_response_from_nearest_area_agent(available_parking(_, _, none, _)) <-
    .print("No available parking found; retrying in 0.5s to area agent of current area");
    .wait(500);
    !request_parking.

+!process_parking_response_from_nearest_area_agent(available_parking(_, _, P, Position)) <-
    .print("Going to parking lot ", P, " at ", Position);
    !go_to_parking_lot(P, Position).

+!process_parking_response_from_nearest_area_agent(timeout) <- // TODO
    .print("Nearest area agent unavailable; retrying to current area agent in 0.5s");
    .wait(500);
    !request_parking.

+!process_parking_response_from_nearest_area_agent(Error) <- // TODO
    .print("Error occurred while contacting nearest area agent: ", Error);
    .wait(500);
    !request_parking.

// ========== Go to parking lot ==========

+!go_to_parking_lot(P, ParkingPosition) <-
    !navigate_to(ParkingPosition, adjacent);
    ?position(X, Y);
    .print("Arrived near parking lot ", P, " at ", ParkingPosition, "; current position: ", position(X, Y));
    -+entry_position(X, Y);
    !request_entry(P, ParkingPosition).

// ========== Navigation ==========

+!navigate_to(DestinationPosition, ArrivalCondition) : position(X, Y)
        & arrived(ArrivalCondition, position(X, Y), DestinationPosition) <-
    true.

+!navigate_to(DestinationPosition, ArrivalCondition) : position(X, Y) <-
    ?preferred_directions(position(X, Y), DestinationPosition, Directions);
    .print("Preferred directions: ", Directions);
    !try_direction(Directions, DestinationPosition, ArrivalCondition).

+!try_direction([Direction | AlternativeDirections], DestinationPosition, ArrivalCondition) <-
    .print("Trying ", Direction);
    move(Direction);
    !navigate_to(DestinationPosition, ArrivalCondition).

-!try_direction([Direction | AlternativeDirections], DestinationPosition, ArrivalCondition) <-
    .print("Direction ", Direction, " blocked; trying another direction");
    !avoid_obstacle(AlternativeDirections, Direction, DestinationPosition, ArrivalCondition).

+!avoid_obstacle([Direction | AlternativeDirections], PreviousDirection, DestinationPosition, ArrivalCondition) <-
    .print("Avoiding obstacle: trying ", Direction);
    move(Direction);
    !continue_in_previous_direction(PreviousDirection, [Direction | AlternativeDirections], DestinationPosition,
        ArrivalCondition).

-!avoid_obstacle([Direction | AlternativeDirections], PreviousDirection, DestinationPosition, ArrivalCondition) <-
    .print("Avoiding obstacle: direction ", Direction, " blocked; trying another direction");
    !avoid_obstacle(AlternativeDirections, PreviousDirection, DestinationPosition, ArrivalCondition).

+!avoid_obstacle([], DestinationPosition, ArrivalCondition) <-
    .print("No available direction to reach ", DestinationPosition,
        "; retrying in 0.5s in case the moves failed due to communication errors");
    .wait(500);
    !navigate_to(DestinationPosition, ArrivalCondition).

+!continue_in_previous_direction(_, _, DestinationPosition, ArrivalCondition) : position(X, Y)
        & arrived(ArrivalCondition, position(X, Y), DestinationPosition) <-
    true.

+!continue_in_previous_direction(PreviousDirection, _, DestinationPosition, ArrivalCondition) <-
    .print("Trying to continue in previous direction: ", PreviousDirection);
    move(PreviousDirection);
    !navigate_to(DestinationPosition, ArrivalCondition).

-!continue_in_previous_direction(PreviousDirection, Directions, DestinationPosition, ArrivalCondition) <-
    .print("Previous direction ", PreviousDirection, " blocked; trying another direction");
    !avoid_obstacle(Directions, PreviousDirection, DestinationPosition, ArrivalCondition).

// ========== Enter parking lot ==========

+!request_entry(P, position(Xp, Yp)) : position(X, Y) & not (X == Xp & Y == Yp) <-
    .print("Requesting entry to parking lot ", P, " at ", position(X, Y));
    .send(P, askOne, entry_status(Status), Answer, 3000);
    !process_entry_response(Answer, P, position(Xp, Yp)).

+!request_entry(P, position(Xp, Yp)) <-
    .print("Already at parking lot ", P, " at ", position(Xp, Yp));
    !stay_in_parking_lot(P).

+!process_entry_response(entry_status(granted), P, _) <-
    .print("Successfully entered parking lot ", P);
    !stay_in_parking_lot(P).

+!process_entry_response(entry_status(denied), P, _) <-
    .print("Entry denied for parking lot ", P);
    !request_parking.

+!process_entry_response(timeout, P, position(Xp, Yp)) <- // TODO to improve with n retries
    .print("Parking agent ", P, " unavailable; retrying in 0.5s");
    .wait(500);
    !request_entry(P, position(Xp, Yp)).

+!process_entry_response(Error, P, position(Xp, Yp)) <- // TODO to improve
    .print("Error occurred while contacting parking agent ", P, ": ", Error);
    .wait(500);
    !request_entry(P, position(Xp, Yp)).

// ========== Leave parking lot ==========

+!stay_in_parking_lot(P) <-
    ?random_between(10000, 20000, Ms);
    .print("Staying in parking lot ", P, " for ", Ms / 1000, " seconds");
    .wait(Ms);
    !leave_parking_lot(P).

+!leave_parking_lot(P) : position(X, Y) & entry_position(EntryX, EntryY) & not (X == EntryX & Y == EntryY) <-
    ?directions(position(X, Y), position(EntryX, EntryY), Direction, _);
    .print("Requesting exit from parking lot ", P, " in direction ", Direction);
    .send(P, askOne, exit(Direction), Answer, 3000);
    !process_exit_response(Answer, P).

+!leave_parking_lot(P) <-
    .print("Already exited from parking lot ", P);
    .print("Going home");
    !go_home.

+!process_exit_response(exit(_), P) <-
    .print("Successfully exited parking lot ", P);
    .print("Going home");
    !go_home.

+!process_exit_response(timeout, P) <-
    .print("Parking agent ", P, " unavailable; retrying in 0.5s");
    .wait(500);
    !leave_parking_lot(P).

+!process_exit_response(Error, P) <- // TODO to improve
    .print("Error occurred while contacting parking agent ", P, ": ", Error);
    .wait(500);
    !leave_parking_lot(P).

// ========== Go home ==========

+!go_home : position(X, Y) & home(HomePosition) <-
    !navigate_to(HomePosition, exact);
    .print("Arrived home").
