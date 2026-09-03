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
    +parking_info(P, Position, Available).

+available(A)[source(P)] : parking_info(P, Position, _) <-
    -+parking_info(P, Position, A);
    .print("Area agent updated parking info for ", P, " at ", Position, " to ", A, " available spots").

// ====================
// TEST-GOAL PLANS
// ====================

+?available_parking(P, Position)[source(_)] : parking_info(P, Position, A) & A > 0 <-
    true. // random parking selection; TODO specific selection
