package parking.model;

public record Position(int x, int y) {

    public Position move(final Direction direction) {
        return switch (direction) {
            case NORTH -> new Position(this.x - 1, this.y);
            case SOUTH -> new Position(this.x + 1, this.y);
            case EAST -> new Position(this.x, this.y + 1);
            case WEST -> new Position(this.x, this.y - 1);
        };
    }
}
