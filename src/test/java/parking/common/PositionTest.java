package parking.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

public class PositionTest {

    private static final Position POSITION = new Position(2, 2);

    @Test
    @DisplayName("Test that moving a position north results in the correct new position")
    public void testMoveNorth() {
        assertEquals(new Position(POSITION.x() - 1, POSITION.y()), POSITION.move(Direction.NORTH));
    }

    @Test
    @DisplayName("Test that moving a position south results in the correct new position")
    public void testMoveSouth() {
        assertEquals(new Position(POSITION.x() + 1, POSITION.y()), POSITION.move(Direction.SOUTH));
    }

    @Test
    @DisplayName("Test that moving a position east results in the correct new position")
    public void testMoveEast() {
        assertEquals(new Position(POSITION.x(), POSITION.y() + 1), POSITION.move(Direction.EAST));
    }

    @Test
    @DisplayName("Test that moving a position west results in the correct new position")
    public void testMoveWest() {
        assertEquals(new Position(POSITION.x(), POSITION.y() - 1), POSITION.move(Direction.WEST));
    }
}
