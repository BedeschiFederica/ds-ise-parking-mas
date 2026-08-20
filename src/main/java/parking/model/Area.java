package parking.model;

public record Area(String id, Position vertex1, Position vertex2) {

    public Area {
        if (vertex1.x() > vertex2.x() || vertex1.y() > vertex2.y()) {
            throw new IllegalArgumentException("Invalid area vertices: " + vertex1 + ", " + vertex2);
        }
    }

    public boolean contains(final Position position) {
        for (int x = this.vertex1.x(); x <= this.vertex2.x(); x++) {
            for (int y = this.vertex1.y(); y <= this.vertex2.y(); y++) {
                if (new Position(x, y).equals(position)) {
                    return true;
                }
            }
        }
        return false;
    }
}
