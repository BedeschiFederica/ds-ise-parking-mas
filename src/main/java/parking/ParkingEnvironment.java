package parking;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import parking.model.City;
import parking.model.CityLoader;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static parking.model.Direction.*;

public class ParkingEnvironment extends Environment {

    // action literals
    public static final Literal moveNorth = Literal.parseLiteral("move("+ NORTH.name().toLowerCase() + ")");
    public static final Literal moveSouth = Literal.parseLiteral("move("+ SOUTH.name().toLowerCase() + ")");
    public static final Literal moveEast = Literal.parseLiteral("move("+ EAST.name().toLowerCase() + ")");
    public static final Literal moveWest = Literal.parseLiteral("move("+ WEST.name().toLowerCase() + ")");

    private City city;

    @Override
    public void init(final String[] args) {
        this.city = new CityLoader().load(args[0]);
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
    public boolean executeAction(final String ag, final Structure action) {
        return true;
    }
}
