// ====================
// PERCEPTS
// ====================

+position(X, Y) : available(A) <-
    .my_name(N);
    .print("Parking agent ", N, " at (", X, ", ", Y, ") with available spots ", A).

// ====================
// PLANS
// ====================

+!start <-
    .print("Parking agent started").

+available(Spots) : area(Agent) <-
    .send(Agent, tell, available(Spots)).

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
