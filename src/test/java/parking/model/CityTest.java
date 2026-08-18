package parking.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class CityTest {

    private static final String AREA_ID = "a1";
    private static final String PARKING_LOT_ID = "p1";
    private static final String DRIVER_ID = "d1";
    private static final Position PARKING_LOT_POSITION = new Position(0, 0);
    private static final Position DRIVER_POSITION = new Position(1, 1);
    private static final String NON_EXISTENT_PARKING_LOT_ID = "p2";
    private static final String NON_EXISTENT_DRIVER_ID = "d2";

    private City city;

    @BeforeEach
    public void init() {
        this.city = new City(3, 3,
                List.of(new Area(AREA_ID, new Position(0, 0), new Position(2, 2))),
                Map.of(PARKING_LOT_ID, PARKING_LOT_POSITION),
                Map.of(DRIVER_ID, DRIVER_POSITION)
        );
    }

    @Test
    @DisplayName("Test that the city returns the correct area for a driver")
    public void testGetDriverArea() {
        assertEquals(AREA_ID, this.city.getDriverArea(DRIVER_ID));
    }

    @Test
    @DisplayName("Test that the city fails to return the area for a non-existent driver")
    public void testCannotGetAreaOfUnexistentDriver() {
        assertThrows(IllegalArgumentException.class, () -> this.city.getDriverArea(NON_EXISTENT_DRIVER_ID));
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
}
