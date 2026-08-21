package parking;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import parking.model.City;
import parking.model.CityLoaderImpl;
import parking.model.Direction;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ParkingEnvironment extends Environment {

    private static final String MOVE_ACTION = "move";
    private static final String AUTHORIZE_ACTION = "authorize";
    private static final String ENTER_ACTION = "enter";

    private City city;

    @Override
    public void init(final String[] args) {
        this.city = new CityLoaderImpl().load(args[0]);
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
                Literal.parseLiteral(String.format("area(%s)", this.city.getDriverArea(agentName))),
                Literal.parseLiteral(String.format("position(%d, %d)",
                        this.city.getDriverPosition(agentName).x(),
                        this.city.getDriverPosition(agentName).y()
                ))
        );
    }

    private Collection<Literal> getParkingAgentPercepts(final String agentName) {
        return List.of(
                Literal.parseLiteral(String.format("position(%d, %d)",
                        this.city.getParkingLotPosition(agentName).x(),
                        this.city.getParkingLotPosition(agentName).y()
                ))
        );
    }

    @Override
    public boolean executeAction(final String agentName, final Structure action) {
        return switch (action.getFunctor()) {
            case MOVE_ACTION -> this.city.moveDriver(agentName, Direction.fromString(action.getTerm(0).toString()));
            case AUTHORIZE_ACTION -> {
                this.city.authorizeDriver(action.getTerm(0).toString(), agentName);
                yield true;
            }
            case ENTER_ACTION -> this.city.enter(agentName, action.getTerm(0).toString());
            default -> throw new IllegalArgumentException("Unknown action: " + action);
        };
    }
}