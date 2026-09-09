package parking.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import parking.common.Direction;
import parking.common.Position;

import java.util.*;

public class CityImplTest {

    private static final int WIDTH = 3;
    private static final int HEIGHT = 3;
    private static final String AREA_ID = "a1";
    private static final ParkingLotId PARKING_LOT_ID = new ParkingLotId("p1");
    private static final DriverId DRIVER_ID = new DriverId("d1");
    private static final Position PARKING_LOT_POSITION = new Position(0, 0);
    private static final Position DRIVER_POSITION = new Position(1, 1);
    private static final ParkingLotId NON_EXISTENT_PARKING_LOT_ID = new ParkingLotId("p2");
    private static final DriverId NON_EXISTENT_DRIVER_ID = new DriverId("d2");
    private static final Position INVALID_POSITION = new Position(3, 3);

    private City city;

    @BeforeEach
    public void init() {
        this.city = new CityImpl(WIDTH, HEIGHT,
                List.of(new Area(AREA_ID, new Position(0, 0), new Position(2, 2))),
                Map.of(PARKING_LOT_ID, PARKING_LOT_POSITION),
                Map.of(DRIVER_ID, DRIVER_POSITION)
        );
    }

    @Test
    @DisplayName("Test that creating a city with an invalid position fails")
    public void testFailCityCreationWithInvalidPosition() {
        assertThrows(IllegalArgumentException.class, () -> new CityImpl(WIDTH, HEIGHT,
                List.of(new Area(AREA_ID, new Position(0, 0), INVALID_POSITION)),
                Map.of(PARKING_LOT_ID, PARKING_LOT_POSITION),
                Map.of(DRIVER_ID, DRIVER_POSITION)
        ));
    }

    @Test
    @DisplayName("Test that the city returns the correct dimensions")
    public void testGetCityDimensions() {
        assertEquals(WIDTH, this.city.getWidth());
        assertEquals(HEIGHT, this.city.getHeight());
    }

    @Test
    @DisplayName("Test that the city returns the correct current area for a driver")
    public void testGetDriverCurrentArea() {
        assertEquals(AREA_ID, this.city.getDriverCurrentArea(DRIVER_ID));
    }

    @Test
    @DisplayName("Test that the city returns the correct nearest area for a driver")
    public void testGetDriverNearestArea() {
        assertEquals(Optional.empty(), this.city.getDriverNearestArea(DRIVER_ID));
        final City cityWith3Areas = city = new CityImpl(4, 4,
                List.of(new Area(AREA_ID, new Position(0, 0), new Position(1, 1)),
                        new Area("a2", new Position(0, 2), new Position(2, 2)),
                        new Area("a3", new Position(0, 3), new Position(3, 3))),
                Map.of(PARKING_LOT_ID, PARKING_LOT_POSITION),
                Map.of(DRIVER_ID, DRIVER_POSITION)
        );
        assertEquals(Optional.of("a2"), cityWith3Areas.getDriverNearestArea(DRIVER_ID));
    }

    @Test
    @DisplayName("Test that the city fails to return areas for a non-existent driver")
    public void testCannotGetAreasOfUnexistentDriver() {
        assertThrows(IllegalArgumentException.class, () -> this.city.getDriverCurrentArea(NON_EXISTENT_DRIVER_ID));
        assertThrows(IllegalArgumentException.class, () -> this.city.getDriverNearestArea(NON_EXISTENT_DRIVER_ID));
    }

    @Test
    @DisplayName("Test that the city returns the correct position for a driver")
    public void testGetDriverPosition() {
        assertEquals(DRIVER_POSITION, this.city.getDriverPosition(DRIVER_ID));
    }

    @Test
    @DisplayName("Test that the city fails to return the position for a non-existent driver")
    public void testCannotGetPositionOfUnexistentDriver() {
        assertThrows(IllegalArgumentException.class, () -> this.city.getDriverPosition(NON_EXISTENT_DRIVER_ID));
    }

    @Test
    @DisplayName("Test that the city returns the correct position for a parking lot")
    public void testGetParkingLotPosition() {
        assertEquals(PARKING_LOT_POSITION, this.city.getParkingLotPosition(PARKING_LOT_ID));
    }

    @Test
    @DisplayName("Test that the city fails to return the position for a non-existent parking lot")
    public void testCannotGetPositionOfUnexistentParkingLot() {
        assertThrows(IllegalArgumentException.class, () -> this.city.getParkingLotPosition(NON_EXISTENT_PARKING_LOT_ID));
    }

    @Test
    @DisplayName("Test that a driver moves correctly")
    public void testDriverMovesCorrectly() {
        assertTrue(this.city.moveDriver(DRIVER_ID, Direction.SOUTH));
        assertEquals(new Position(DRIVER_POSITION.x() + 1, DRIVER_POSITION.y()),
                this.city.getDriverPosition(DRIVER_ID));
    }

    @Test
    @DisplayName("Test that a driver can't move out of bounds")
    public void testDriverCannotMoveOutOfCityBounds() {
        assertTrue(this.city.moveDriver(DRIVER_ID, Direction.NORTH));
        assertFalse(this.city.moveDriver(DRIVER_ID, Direction.NORTH));
    }

    @Test
    @DisplayName("Test that a driver can't move when inside a parking lot")
    public void testDriverCannotMoveWhenInsideAParkingLot() {
        assertTrue(this.enter());
        assertFalse(this.city.moveDriver(DRIVER_ID, Direction.EAST));
    }

    private boolean enter() {
        this.city.moveDriver(DRIVER_ID, Direction.NORTH);
        return this.city.enter(DRIVER_ID, PARKING_LOT_ID);
    }

    @Test
    @DisplayName("Test that a driver enters a parking lot correctly")
    public void testDriverEntersParkingLot() {
        assertTrue(this.enter());
        assertEquals(PARKING_LOT_POSITION, this.city.getDriverPosition(DRIVER_ID));
    }

    @Test
    @DisplayName("Test that a driver can't enter a parking lot if it's not adjacent to it")
    public void testDriverCannotEnterParkingLotIfNotAdjacent() {
        assertFalse(this.city.enter(DRIVER_ID, PARKING_LOT_ID));
        assertNotEquals(PARKING_LOT_POSITION, this.city.getDriverPosition(DRIVER_ID));
    }

    @Test
    @DisplayName("Test that a driver exits from a parking lot correctly")
    public void testDriverExitsParkingLot() {
        assertTrue(this.enter());
        assertTrue(this.city.exit(DRIVER_ID, PARKING_LOT_ID, Direction.EAST));
        assertEquals(new Position(PARKING_LOT_POSITION.x(), PARKING_LOT_POSITION.y() + 1),
                this.city.getDriverPosition(DRIVER_ID));
    }

    @Test
    @DisplayName("Test that a driver can't exit from a parking lot when not inside it")
    public void testDriverCannotExitParkingLotWhenNotInsideIt() {
        assertFalse(this.city.exit(DRIVER_ID, PARKING_LOT_ID, Direction.EAST));
    }

    @Test
    @DisplayName("Test that a driver can't move out of the city bounds when exiting from a parking lot")
    public void testDriverCannotMoveOutOfBoundsWhenExitingParkingLot() {
        assertTrue(this.enter());
        assertFalse(this.city.exit(DRIVER_ID, PARKING_LOT_ID, Direction.NORTH));
        assertFalse(this.city.exit(DRIVER_ID, PARKING_LOT_ID, Direction.WEST));
    }

    @Test
    @DisplayName("Test that the city returns the correct amount of occupiers")
    public void testGetOccupiers() {
        assertEquals(WIDTH * HEIGHT, this.city.getOccupiers().size());
    }
}
