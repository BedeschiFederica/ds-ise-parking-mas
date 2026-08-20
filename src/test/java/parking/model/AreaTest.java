package parking.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

public class AreaTest {

    private static final Position IN_POSITION =  new Position(1, 1);
    private static final Position OUT_POSITION =  new Position(3, 2);

    private Area area;

    @BeforeEach
    public void init() {
        this.area = new Area("a1", new Position(0, 0), new Position(2, 2));
    }

    @Test
    @DisplayName("Test that creating an area with invalid vertices fails")
    public void testFailAreaCreationWithInvalidVertices() {
        assertThrows(IllegalArgumentException.class, () -> new Area("a1", new Position(2, 2), new Position(0, 0)));
    }

    @Test
    @DisplayName("Test that the area contains a position within its bounds")
    public void testAreaContainsPosition() {
        assertTrue(this.area.contains(IN_POSITION));
    }

    @Test
    @DisplayName("Test that the area does not contain a position outside its bounds")
    public void testAreaDoesNotContainPosition() {
        assertFalse(this.area.contains(OUT_POSITION));
    }
}
