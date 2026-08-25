package parking;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import parking.model.*;
import parking.view.CityGUI;
import parking.view.CityView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ParkingEnvironment extends Environment {

    private static final String MOVE_ACTION = "move";
    private static final String AUTHORIZE_ACTION = "authorize";
    private static final String ENTER_ACTION = "enter";

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
            case 'd' -> this.getDriverAgentPercepts(agentName);
            case 'p' -> this.getParkingAgentPercepts(agentName);
            case 'a' -> Collections.emptyList();
            default -> throw new IllegalArgumentException("Unknown agent: " + agentName);
        };
    }

    private Collection<Literal> getDriverAgentPercepts(final String agentName) {
        return List.of(
                Literal.parseLiteral(String.format("area(%s)", this.city.getDriverArea(new DriverId(agentName)))),
                Literal.parseLiteral(String.format("position(%d, %d)",
                        this.city.getDriverPosition(new DriverId(agentName)).x(),
                        this.city.getDriverPosition(new DriverId(agentName)).y()
                ))
        );
    }

    private Collection<Literal> getParkingAgentPercepts(final String agentName) {
        return List.of(
                Literal.parseLiteral(String.format("position(%d, %d)",
                        this.city.getParkingLotPosition(new ParkingLotId(agentName)).x(),
                        this.city.getParkingLotPosition(new ParkingLotId(agentName)).y()
                ))
        );
    }

    @Override
    public boolean executeAction(final String agentName, final Structure action) {
        final boolean result = switch (action.getFunctor()) {
            case MOVE_ACTION ->
                    this.city.moveDriver(new DriverId(agentName), Direction.fromString(action.getTerm(0).toString()));
            case AUTHORIZE_ACTION -> {
                this.city.authorizeDriver(new DriverId(action.getTerm(0).toString()), new ParkingLotId(agentName));
                yield true;
            }
            case ENTER_ACTION ->
                    this.city.enter(new DriverId(agentName), new ParkingLotId(action.getTerm(0).toString()));
            default -> throw new IllegalArgumentException("Unknown action: " + action);
        };
        this.updateView();
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException ignored) {}
        return result;
    }

    private void updateView() {
        this.view.update(this.city.getOccupiers());
    }
}