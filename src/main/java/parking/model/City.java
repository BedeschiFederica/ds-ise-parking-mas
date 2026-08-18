package parking.model;

import java.util.List;
import java.util.Map;

public class City {

    private final int width;
    private final int height;
    private final List<Area> areas;
    private final Map<String, Position> parkingLots;
    private final Map<String, Position> drivers;

    public City(final int width, final int height, final List<Area> areas,
                final Map<String, Position> parkingLots, final Map<String, Position> drivers) {
        this.width = width;
        this.height = height;
        this.areas = List.copyOf(areas);
        this.parkingLots = Map.copyOf(parkingLots);
        this.drivers = Map.copyOf(drivers);
        System.out.println("Width: " + this.width + ", height: " + this.height);
        System.out.println("Areas: " + this.areas);
        System.out.println("Parking Lots: " + this.parkingLots);
        System.out.println("Drivers: " + this.drivers);
    }

    public String getDriverArea(final String id) {
        this.requireDriverExistence(id);
        return this.areas.stream()
                .filter(area -> area.contains(this.drivers.get(id)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Driver " + id + " is not in any area"))
                .id();
    }

    public Position getDriverPosition(final String id) {
        this.requireDriverExistence(id);
        return this.drivers.get(id);
    }

    private void requireDriverExistence(final String id) {
        if (this.drivers.get(id) == null) {
            throw new IllegalArgumentException("No such driver: " + id);
        }
    }

    public Position getParkingLotPosition(final String id) {
        this.requireParkingLotExistence(id);
        return this.parkingLots.get(id);
    }

    private void requireParkingLotExistence(final String id) {
        if (this.parkingLots.get(id) == null) {
            throw new IllegalArgumentException("No such parking lot: " + id);
        }
    }
}
