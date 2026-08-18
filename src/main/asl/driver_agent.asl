+!start <-
    .my_name(N);
    .print("Driver agent ", N, " started").

+area(A) <-
    .my_name(N);
    .print("Driver ", N, " in area ", A).

+position(X, Y) <-
    .my_name(N);
    .print("Driver ", N, " at (", X, ", ", Y, ")").