package parking.model;

import parking.common.Direction;
import parking.common.Occupier;
import parking.common.OccupierType;
import parking.common.Position;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.*;

public class CityImpl implements City {

    private final int width;
    private final int height;
    private final List<Area> areas;
    private final Map<ParkingLotId, Position> parkingLots;
    private final Map<DriverId, Position> drivers;

    CityImpl(final int width, final int height, final List<Area> areas,
             final Map<ParkingLotId, Position> parkingLots, final Map<DriverId, Position> drivers) {
        this.width = width;
        this.height = height;
        this.areas = List.copyOf(areas);
        this.parkingLots = Map.copyOf(parkingLots);
        this.drivers = new ConcurrentHashMap<>(drivers);
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
        return position.x() < 0 || position.x() >= this.height || position.y() < 0 || position.y() >= this.width;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public String getDriverArea(final DriverId id) {
        this.requireDriverExistence(id);
        return this.areas.stream()
                .filter(area -> area.contains(this.drivers.get(id)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Driver " + id + " is not in any area"))
                .id();
    }

    @Override
    public Position getDriverPosition(final DriverId id) {
        this.requireDriverExistence(id);
        return this.drivers.get(id);
    }

    private void requireDriverExistence(final DriverId id) {
        if (!this.drivers.containsKey(id)) {
            throw new IllegalArgumentException("No such driver: " + id);
        }
    }

    @Override
    public Position getParkingLotPosition(final ParkingLotId id) {
        this.requireParkingLotExistence(id);
        return this.parkingLots.get(id);
    }

    private void requireParkingLotExistence(final ParkingLotId id) {
        if (!this.parkingLots.containsKey(id)) {
            throw new IllegalArgumentException("No such parking lot: " + id);
        }
    }

    @Override
    public synchronized boolean moveDriver(final DriverId id, final Direction direction) {
        this.requireDriverExistence(id);
        final Position newPosition = this.drivers.get(id).move(direction);
        if (this.isOutOfBounds(newPosition)) {
            return false;
        }
        this.drivers.replace(id, newPosition);
        return true;
    }

    @Override
    public synchronized boolean enter(final DriverId driverId, final ParkingLotId parkingLotId) {
        this.requireDriverExistence(driverId);
        this.requireParkingLotExistence(parkingLotId);
        if (!this.areAdjacent(driverId, parkingLotId)) {
            return false;
        }
        this.drivers.replace(driverId, this.parkingLots.get(parkingLotId));
        return true;
    }

    private boolean areAdjacent(final DriverId driverId, final ParkingLotId parkingLotId) {
        final Position driverPos = this.drivers.get(driverId);
        final Position parkingPos = this.parkingLots.get(parkingLotId);
        return (driverPos.x() == parkingPos.x() && Math.abs(driverPos.y() - parkingPos.y()) == 1)
                || (driverPos.y() == parkingPos.y() && Math.abs(driverPos.x() - parkingPos.x()) == 1);
    }

    @Override
    public Map<Position, Set<Occupier>> getOccupiers() {
        final Map<Position, Set<Occupier>> occupiers =
                Stream.concat(
                        this.parkingLots.entrySet().stream()
                                .map(e -> Map.entry(
                                        e.getValue(),
                                        new Occupier(e.getKey().id(), OccupierType.PARKING)
                                )),
                        this.drivers.entrySet().stream()
                                .map(e -> Map.entry(
                                        e.getValue(),
                                        new Occupier(e.getKey().id(), OccupierType.DRIVER)
                                ))
                ).collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())
                )
        );
        this.areas.stream()
                .flatMap(area -> area.getPositions().stream().map(position ->
                        Map.entry(position, new Occupier(area.id(), OccupierType.AREA))))
                .forEach(entry -> occupiers.putIfAbsent(entry.getKey(), Set.of(entry.getValue())));
        return occupiers;
    }
}
