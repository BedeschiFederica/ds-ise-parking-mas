// ====================
// BELIEFS AND RULES
// ====================

adjacent(position(X1, Y1), position(X2, Y2)) :-
    (X1 == X2 & (Y1 == Y2 + 1 | Y1 == Y2 - 1)) | (Y1 == Y2 & (X1 == X2 + 1 | X1 == X2 - 1)).

// ====================
// PERCEPTS
// ====================

+area(A) <-
    .my_name(N);
    .print("Driver ", N, " in area ", A).

+position(X, Y) <-
    .my_name(N);
    .print("Driver ", N, " at (", X, ", ", Y, ")").

// ====================
// PLANS
// ====================

+!start <-
    .my_name(N);
    .print("Driver agent ", N, " started");
    !request_parking.

// ========== Request parking to Area Agent ==========

+!request_parking : area(A) <-
    .my_name(N);
    .print("Driver ", N, " requesting parking to area ", A);
    .send(A, askOne, available_parking(P, Position), Answer, 3000);
    !process_parking_response(Answer).

+!process_parking_response(available_parking(P, Position)) <-
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

// ========== Go to parking lot ==========

+!go_to_parking_lot(P, position(Xp, Yp)) : position(X, Y) & adjacent(position(X, Y), position(Xp, Yp)) <-
    .print("Arrived near parking lot ", P, " at ", position(Xp, Yp), "; current position: ", position(X, Y));
    !request_entry(P).

+!go_to_parking_lot(P, position(Xp, Yp)) : position(X, Y) & X > Xp <-
    move(north);
    .print("Going north");
    !go_to_parking_lot(P, position(Xp, Yp)).

+!go_to_parking_lot(P, position(Xp, Yp)) : position(X, Y) & X < Xp <-
    move(south);
    .print("Going south");
    !go_to_parking_lot(P, position(Xp, Yp)).

+!go_to_parking_lot(P, position(Xp, Yp)) : position(X, Y) & Y < Yp <-
    move(east);
    .print("Going east");
    !go_to_parking_lot(P, position(Xp, Yp)).

+!go_to_parking_lot(P, position(Xp, Yp)) : position(X, Y) & Y > Yp <-
    move(west);
    .print("Going west");
    !go_to_parking_lot(P, position(Xp, Yp)).

-!go_to_parking_lot(P, position(Xp, Yp)) <-
    .print("Failed to move to parking lot ", P, " at ", position(Xp, Yp), "; retrying in 0.5s");
    .wait(500);
    !go_to_parking_lot(P, position(Xp, Yp)).

// ========== Enter parking lot ==========

+!request_entry(P) <-
    .print("Requesting entry to parking lot ", P);
    .send(P, askOne, entry_status(Status), Answer, 3000);
    !process_entry_response(Answer, P).

+!process_entry_response(entry_status(granted), P) <-
    .print("Successfully entered parking lot ", P).

+!process_entry_response(entry_status(denied), P) <-
    .print("Entry denied for parking lot ", P);
    !request_parking.

+!process_entry_response(timeout, P) <- // TODO to improve
    .print("Parking agent ", P, " unavailable; retrying in 0.5s");
    .wait(500);
    !request_entry(P).

+!process_entry_response(Error, P) <- // TODO to improve
    .print("Error occurred while contacting parking agent ", P, ": ", Error);
    .wait(500);
    !request_entry(P).
