package parking.model;

import java.util.*;
import java.util.stream.*;

public class CityImpl implements City {

    private final int width;
    private final int height;
    private final List<Area> areas;
    private final Map<String, Position> parkingLots;
    private final Map<String, Position> drivers;

    public CityImpl(final int width, final int height, final List<Area> areas,
                    final Map<String, Position> parkingLots, final Map<String, Position> drivers) {
        this.width = width;
        this.height = height;
        this.areas = List.copyOf(areas);
        this.parkingLots = Map.copyOf(parkingLots);
        this.drivers = new HashMap<>(drivers);
        this.validatePositions();
        System.out.println("Width: " + this.width + ", height: " + this.height);
        System.out.println("Areas: " + this.areas);
        System.out.println("Parking Lots: " + this.parkingLots);
        System.out.println("Drivers: " + this.drivers);
    }

    private void validatePositions() {
        Stream.concat(
                this.areas.stream().flatMap(area -> Stream.of(area.vertex1(), area.vertex2())),
                Stream.concat(this.parkingLots.values().stream(), this.drivers.values().stream())
        ).collect(Collectors.toSet()).forEach(position -> {
            if (this.isOutOfBounds(position)) {
                throw new IllegalArgumentException("Position out of bounds: " + position);
            }
        });
    }

    private boolean isOutOfBounds(final Position position) {
        return position.x() < 0 || position.x() >= this.width || position.y() < 0 || position.y() >= this.height;
    }

    @Override
    public String getDriverArea(final String id) {
        this.requireDriverExistence(id);
        return this.areas.stream()
                .filter(area -> area.contains(this.drivers.get(id)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Driver " + id + " is not in any area"))
                .id();
    }

    @Override
    public Position getDriverPosition(final String id) {
        this.requireDriverExistence(id);
        return this.drivers.get(id);
    }

    private void requireDriverExistence(final String id) {
        if (this.drivers.get(id) == null) {
            throw new IllegalArgumentException("No such driver: " + id);
        }
    }

    @Override
    public Position getParkingLotPosition(final String id) {
        this.requireParkingLotExistence(id);
        return this.parkingLots.get(id);
    }

    private void requireParkingLotExistence(final String id) {
        if (this.parkingLots.get(id) == null) {
            throw new IllegalArgumentException("No such parking lot: " + id);
        }
    }

    @Override
    public boolean moveDriver(final String id, final Direction direction) {
        this.requireDriverExistence(id);
        if (this.isOutOfBounds(this.drivers.get(id).move(direction))) {
            return false;
        }
        this.drivers.replace(id, this.drivers.get(id).move(direction));
        return true;
    }
}
