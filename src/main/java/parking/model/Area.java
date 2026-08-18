package parking.model;

public record Area(String id, Position vertex1, Position vertex2) {

    public boolean contains(final Position position) {
        for (int x = vertex1.x(); x <= vertex2.x(); x++) {
            for (int y = vertex1.y(); y <= vertex2.y(); y++) {
                if (new Position(x, y).equals(position)) {
                    return true;
                }
            }
        }
        return false;
    }
}
