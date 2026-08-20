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
    .wait(2000);
    !request_parking.

// ========== Request parking to Area Agent ==========

+!request_parking : area(A) <-
    .my_name(N);
    .print("Driver ", N, " requesting parking to area ", A);
    .send(A, askOne, available_parking(P, Position), Answer, 3000);
    !process_parking_response(Answer).

+!process_parking_response(timeout) <-
    .print("Area agent unavailable"). // TODO to handle

+!process_parking_response(available_parking(P, Position)) <-
    .print("Going to parking lot ", P, " at ", Position);
    !go_to_parking_lot(P, Position).

// ========== Go to parking lot ==========

+!go_to_parking_lot(P, position(Xp, Yp)) : position(X, Y) & adjacent(position(X, Y), position(Xp, Yp)) <-
    .print("Arrived near parking lot ", P, " at ", position(Xp, Yp), "; current position: ", position(X, Y)).
    // TODO

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