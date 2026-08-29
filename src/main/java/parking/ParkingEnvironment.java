package parking;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;

import parking.common.Direction;
import parking.model.*;
import parking.view.CityGUI;
import parking.view.CityView;

import java.util.*;

public class ParkingEnvironment extends Environment {

    private static final String MOVE_ACTION = "move";
    private static final String ENTER_ACTION = "enter_driver";

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
        return List.of(
                Literal.parseLiteral(String.format("area(%s)", this.city.getDriverArea(driverId))),
                Literal.parseLiteral(String.format("position(%d, %d)",
                        this.city.getDriverPosition(driverId).x(),
                        this.city.getDriverPosition(driverId).y()
                ))
        );
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
                    this.city.moveDriver(new DriverId(agentName), Direction.fromString(action.getTerm(0).toString()));
            case ENTER_ACTION ->
                    this.city.enter(new DriverId(action.getTerm(0).toString()), new ParkingLotId(agentName));
            default -> throw new IllegalArgumentException("Unknown action: " + action);
        };
        this.updateView();
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException ignored) {}
        return success;
    }

    private void updateView() {
        this.view.update(this.city.getOccupiers());
    }
}