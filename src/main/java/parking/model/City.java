package parking.model;

public interface City {

    /**
     * Gets the area in which the driver is located.
     *
     * @param id driver's id
     * @return the id of the area in which the driver is located.
     */
    String getDriverArea(final String id);

    /**
     * Gets driver position.
     *
     * @param id driver's id
     * @return the position of the driver.
     */
    Position getDriverPosition(final String id);

    /**
     * Gets parking lot position.
     *
     * @param id parking lot's id
     * @return the position of the parking lot.
     */
    Position getParkingLotPosition(final String id);

    /**
     * Moves the specified driver in the specified direction.
     *
     * @param id driver's id
     * @param direction the direction in which to move the driver
     * @return true if the driver was moved successfully, false otherwise.
     */
    boolean moveDriver(final String id, final Direction direction);
}
