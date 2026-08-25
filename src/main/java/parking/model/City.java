package parking.model;

import parking.common.Direction;
import parking.common.Occupier;
import parking.common.Position;

import java.util.Map;

public interface City {

    /**
     * Gets city width.
     *
     * @return the width of the city.
     */
    int getWidth();

    /**
     * Gets city height.
     *
     * @return the height of the city.
     */
    int getHeight();

    /**
     * Gets the area in which the driver is located.
     *
     * @param id driver's id
     * @return the id of the area in which the driver is located.
     */
    String getDriverArea(DriverId id);

    /**
     * Gets driver position.
     *
     * @param id driver's id
     * @return the position of the driver.
     */
    Position getDriverPosition(DriverId id);

    /**
     * Gets parking lot position.
     *
     * @param id parking lot's id
     * @return the position of the parking lot.
     */
    Position getParkingLotPosition(ParkingLotId id);

    /**
     * Moves the specified driver in the specified direction.
     *
     * @param id driver's id
     * @param direction the direction in which to move the driver
     * @return true if the driver was moved successfully, false otherwise.
     */
    boolean moveDriver(DriverId id, Direction direction);

    /**
     * Authorizes the specified driver to enter the specified parking lot.
     *
     * @param driverId the id of the driver
     * @param parkingLotId the id of the parking lot.
     */
    void authorizeDriver(DriverId driverId, ParkingLotId parkingLotId);

    /**
     * Makes the specified driver enter the specified parking lot.
     *
     * @param driverId the id of the driver
     * @param parkingLotId the id of the parking lot
     * @return true if the driver entered successfully, false otherwise.
     */
    boolean enter(DriverId driverId, ParkingLotId parkingLotId);

    /**
     * Gets the occupiers of the city.
     *
     * @return a map containing each position and its occupier.
     */
    Map<Position, Occupier> getOccupiers();
}
