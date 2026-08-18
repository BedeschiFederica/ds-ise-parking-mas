+!start <-
    .print("Parking agent started").

+position(X, Y) : capacity(C) <-
    .my_name(N);
    .print("Parking agent ", N, " at (", X, ", ", Y, ") with capacity ", C).