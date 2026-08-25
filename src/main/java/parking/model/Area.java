package parking.model;

import parking.common.Position;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

record Area(String id, Position vertex1, Position vertex2) {

    Area {
        if (vertex1.x() > vertex2.x() || vertex1.y() > vertex2.y()) {
            throw new IllegalArgumentException("Invalid area vertices: " + vertex1 + ", " + vertex2);
        }
    }

    boolean contains(final Position position) {
        return this.getPositions().contains(position);
    }

    Set<Position> getPositions() {
        return IntStream.rangeClosed(this.vertex1.x(), this.vertex2.x())
                .boxed()
                .flatMap(x ->
                        IntStream.rangeClosed(this.vertex1.y(), this.vertex2.y()).mapToObj(y -> new Position(x, y)))
                .collect(Collectors.toSet());
    }
}
