package parking.model;

public interface City {

    /**
     * Gets the area in which the driver is located.
     *
     * @param id driver's id
     * @return the id of the area in which the driver is located.
     */
    String getDriverArea(String id);

    /**
     * Gets driver position.
     *
     * @param id driver's id
     * @return the position of the driver.
     */
    Position getDriverPosition(String id);

    /**
     * Gets parking lot position.
     *
     * @param id parking lot's id
     * @return the position of the parking lot.
     */
    Position getParkingLotPosition(String id);

    /**
     * Moves the specified driver in the specified direction.
     *
     * @param id driver's id
     * @param direction the direction in which to move the driver
     * @return true if the driver was moved successfully, false otherwise.
     */
    boolean moveDriver(String id, Direction direction);

    /**
     * Authorizes the specified driver to enter the specified parking lot.
     *
     * @param driverId the id of the driver
     * @param parkingLotId the id of the parking lot.
     */
    void authorizeDriver(String driverId, String parkingLotId);

    /**
     * Makes the specified driver enter the specified parking lot.
     *
     * @param driverId the id of the driver
     * @param parkingLotId the id of the parking lot
     * @return true if the driver entered successfully, false otherwise.
     */
    boolean enter(String driverId, String parkingLotId);
}
