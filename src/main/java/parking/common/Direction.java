package parking.common;

public enum Direction {
    NORTH, SOUTH, EAST, WEST;

    public static Direction fromString(final String value) {
        return Direction.valueOf(value.toUpperCase());
    }
}
