// ====================
// BELIEFS AND RULES
// ====================

adjacent(position(X1, Y1), position(X2, Y2)) :-
    (X1 == X2 & (Y1 == Y2 + 1 | Y1 == Y2 - 1)) | (Y1 == Y2 & (X1 == X2 + 1 | X1 == X2 - 1)).

direction(position(FromX, FromY), position(ToX, ToY), north) :- FromX > ToX.
direction(position(FromX, FromY), position(ToX, ToY), south) :- FromX < ToX.
direction(position(FromX, FromY), position(ToX, ToY), east) :- FromY < ToY.
direction(position(FromX, FromY), position(ToX, ToY), west) :- FromY > ToY.

random_between(X, Y, X + math.floor(R * (Y - X + 1))) :- .random(R).

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

+!go_to_parking_lot(P, position(Xp, Yp)) : position(X, Y) & adjacent(position(X, Y), position(Xp, Yp)) <-
    .print("Arrived near parking lot ", P, " at ", position(Xp, Yp), "; current position: ", position(X, Y));
    -+entry_position(X, Y);
    !request_entry(P, position(Xp, Yp)).

+!go_to_parking_lot(P, position(Xp, Yp)) : position(X, Y) <-
    ?direction(position(X, Y), position(Xp, Yp), Direction);
    move(Direction);
    .print("Going ", Direction);
    !go_to_parking_lot(P, position(Xp, Yp)).

-!go_to_parking_lot(P, position(Xp, Yp)) <-
    .print("Failed to move to parking lot ", P, " at ", position(Xp, Yp), "; retrying in 0.5s");
    .wait(500);
    !go_to_parking_lot(P, position(Xp, Yp)).

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
    ?direction(position(X, Y), position(EntryX, EntryY), Direction);
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

+!go_home : position(X, Y) & home(position(HomeX, HomeY)) & X == HomeX & Y == HomeY <-
    .print("Arrived home").

+!go_home : position(X, Y) & home(HomePosition) <-
    ?direction(position(X, Y), HomePosition, Direction);
    move(Direction);
    .print("Going ", Direction);
    !go_home.

-!go_home <-
    .print("Failed to move while going home; retrying in 0.5s");
    .wait(500);
    !go_home.
