package parking;

import jason.asSyntax.*;
import jason.environment.Environment;

import parking.common.Direction;
import parking.model.*;
import parking.view.CityGUI;
import parking.view.CityView;

import java.util.*;
import java.util.stream.Stream;

public class ParkingEnvironment extends Environment {

    static final String MOVE_ACTION = "move";
    static final String ENTER_ACTION = "enter_driver";
    static final String EXIT_ACTION = "exit_driver";

    private static final long ACTION_DELAY_IN_MS = 1000L;

    private City city;
    private CityView view;

    @Override
    public void init(final String[] args) {
        this.city = new CityLoaderImpl().load(args[0]);
        this.view = new CityGUI(this.city.getWidth(), this.city.getHeight());
        this.updateView();
        this.view.setVisible(true);
    }

    @Override
    public Collection<Literal> getPercepts(final String agentName) {
        return switch (agentName.charAt(0)) {
            case 'd' -> this.getDriverAgentPercepts(new DriverId(agentName));
            case 'p' -> this.getParkingAgentPercepts(new ParkingLotId(agentName));
            case 'a' -> Collections.emptyList();
            default -> throw new IllegalArgumentException("Unknown agent: " + agentName);
        };
    }

    private Collection<Literal> getDriverAgentPercepts(final DriverId driverId) {
        return Stream.concat(
                Stream.of(
                        Literal.parseLiteral(String.format("position(%d, %d)",
                                this.city.getDriverPosition(driverId).x(),
                                this.city.getDriverPosition(driverId).y()
                        )),
                        Literal.parseLiteral(String.format("current_area(%s)",
                                this.city.getDriverCurrentArea(driverId)))
                ),
                this.city.getDriverNearestArea(driverId).stream().map(areaId ->
                        Literal.parseLiteral(String.format("nearest_area(%s)", areaId)))
        ).toList();
    }

    private Collection<Literal> getParkingAgentPercepts(final ParkingLotId parkingLotId) {
        return List.of(
                Literal.parseLiteral(String.format("position(%d, %d)",
                        this.city.getParkingLotPosition(parkingLotId).x(),
                        this.city.getParkingLotPosition(parkingLotId).y()
                ))
        );
    }

    @Override
    public boolean executeAction(final String agentName, final Structure action) {
        final boolean success = switch (action.getFunctor()) {
            case MOVE_ACTION ->
                    this.city.moveDriver(new DriverId(agentName), this.getDirectionFrom(action.getTerm(0)));
            case ENTER_ACTION ->
                    this.city.enter(new DriverId(action.getTerm(0).toString()), new ParkingLotId(agentName));
            case EXIT_ACTION ->
                    this.city.exit(new DriverId(action.getTerm(0).toString()), new ParkingLotId(agentName),
                            this.getDirectionFrom(action.getTerm(1)));
            default -> throw new IllegalArgumentException("Unknown action: " + action);
        };
        this.updateView();
        try {
            Thread.sleep(ACTION_DELAY_IN_MS);
        } catch (final InterruptedException ignored) {}
        return success;
    }

    private void updateView() {
        this.view.update(this.city.getOccupiers());
    }

    private Direction getDirectionFrom(final Term term) {
        return Direction.fromString(term.toString());
    }
}