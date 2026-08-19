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
    .send(P, askOne, parking_info(Position), Answer, 3000);
    !process_parking_response(Answer, P).

+!process_parking_response(timeout, P) <-
    .print("Parking agent ", P, " unavailable").

+!process_parking_response(parking_info(Position), P) <-
    .print("Parking agent ", P, " at ", Position);
    +parking_info(P, Position).

+?available_parking(P, Position)[source(_)] : parking_info(P, Position) <-
    true. // random parking selection; TODO specific selection
