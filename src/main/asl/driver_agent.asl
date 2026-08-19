+!start <-
    .my_name(N);
    .print("Driver agent ", N, " started");
    .wait(2000);
    !request_parking.

+area(A) <-
    .my_name(N);
    .print("Driver ", N, " in area ", A).

+position(X, Y) <-
    .my_name(N);
    .print("Driver ", N, " at (", X, ", ", Y, ")").

+!request_parking : area(A) <-
    .my_name(N);
    .print("Driver ", N, " requesting parking to area ", A);
    .send(A, askOne, available_parking(P, Position), Answer, 3000);
    !process_response(Answer).

+!process_response(timeout) <-
    .print("Area agent unavailable"). // TODO to handle

+!process_response(available_parking(P, Position)) <-
    .print("Going to parking lot ", P, " at ", Position). // TODO