// ====================
// PERCEPTS
// ====================

+position(X, Y) : available(A) <-
    .print("Parking agent at (", X, ", ", Y, ") with ", A, " available spots").

// ====================
// PLANS
// ====================

+!start <-
    .print("Parking agent started").

+available(Spots) : area(Agent) <-
    .println("Sending availability update to area agent ", Agent, " with ", Spots, " available spots");
    .my_name(P);
    .send(Agent, achieve, update_availability(P, Spots)).

// ====================
// TEST-GOAL PLANS
// ====================

+?parking_info(position(X, Y), A) : position(X, Y) & available(A) <-
    true.

@[atomic]
+?entry_status(granted)[source(D)] : available(A) & A > 0 <-
    .print("Processing entry request from driver ", D, "; available spots: ", A);
    enter_driver(D);
    .print("Entry granted for driver ", D, "; remaining spots: ", A - 1);
    -+available(A - 1).

@[atomic]
+?entry_status(denied)[source(D)] <-
    .print("Entry denied for driver ", D, "; no available spots.").

@[atomic]
+?exit(Direction)[source(D)] : available(A) <-
    .print("Processing exit request from driver ", D, "; available spots: ", A);
    exit_driver(D, Direction);
    .print("Driver ", D, " exited ", Direction, "; available spots: ", A + 1);
    -+available(A + 1).
