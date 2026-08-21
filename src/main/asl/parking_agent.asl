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

// ====================
// TEST-GOAL PLANS
// ====================

+?parking_info(position(X, Y)) : position(X, Y) <-
    true.

+?entry_status(granted)[source(D)] : available(A) & A > 0 <-
    .print("Authorizing entry to driver ", D, "; available spots: ", A);
     authorize(D).

+?entry_status(denied)[source(D)] : available(A) & A = 0 <-
    .print("Entry denied for driver ", D, "; no available spots.").
