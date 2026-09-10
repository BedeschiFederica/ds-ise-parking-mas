package parking;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import jason.asSyntax.*;
import parking.common.Direction;
import parking.common.Position;

public class ParkingEnvironmentTest {

    private final String DRIVER_AGENT = "d1";
    private final String PARKING_AGENT = "p1";
    private final String POSITION_REGEX = "position\\(\\d+,\\s*\\d+\\)";
    private final String CURRENT_AREA_REGEX = "current_area\\(.*\\)";

    private ParkingEnvironment environment;

    @BeforeEach
    public void init() {
        this.environment = new ParkingEnvironment();
        this.environment.init(new String[]{"envTest.json"});
    }

    @Test
    @DisplayName("Test that initializing a parking environment providing a non-existent file fails")
    public void testFailCityCreationWithInvalidPosition() {
        assertThrows(RuntimeException.class, () ->
                new ParkingEnvironment().init(new String[]{"non_existent_file.json"}));
    }

    @Test
    @DisplayName("Test that the environment returns the correct percepts for a driver agent")
    public void testGetDriverAgentPercepts() {
        assertAll(() -> {
            assertTrue(this.environment.getPercepts(DRIVER_AGENT).stream()
                    .anyMatch(percept -> percept.toString().matches(POSITION_REGEX)));
            assertTrue(this.environment.getPercepts(DRIVER_AGENT).stream()
                    .anyMatch(percept -> percept.toString().matches(CURRENT_AREA_REGEX)));
        });
    }

    @Test
    @DisplayName("Test that the environment returns the correct percepts for a parking agent")
    public void testGetParkingAgentPercepts() {
        assertTrue(this.environment.getPercepts(PARKING_AGENT).stream()
                .anyMatch(percept -> percept.toString().matches(POSITION_REGEX)));
    }

    @Test
    @DisplayName("Test that the environment returns the correct percepts for an area agent")
    public void testGetAreaAgentPercepts() {
        assertTrue(this.environment.getPercepts("a1").isEmpty());
    }

    private Position getPosition(final String agentName) {
        return this.environment.getPercepts(agentName).stream()
                .filter(percept -> percept.toString().matches(POSITION_REGEX))
                .map(percept -> new Position(
                        Integer.parseInt(percept.getTerm(0).toString()),
                        Integer.parseInt(percept.getTerm(1).toString())))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Position percept not found"));
    }

    private Structure createMoveAction(final Direction direction) {
        return ASSyntax.createStructure(ParkingEnvironment.MOVE_ACTION,
                ASSyntax.createAtom(direction.toString().toLowerCase()));
    }

    private boolean executeMoveAction(final String driver, final Direction direction) {
        return this.environment.executeAction(driver, this.createMoveAction(direction));
    }

    @Test
    @DisplayName("Test that executing a move action updates the driver position correctly")
    public void testExecuteMoveAction() {
        final Position driverPosition = this.getPosition(DRIVER_AGENT);
        assertTrue(this.executeMoveAction(DRIVER_AGENT, Direction.EAST));
        final Position newDriverPosition = this.getPosition(DRIVER_AGENT);
        assertEquals(driverPosition.move(Direction.EAST), newDriverPosition);
    }

    @Test
    @DisplayName("Test that executing a move action returns false when trying to move out of bounds")
    public void testExecutingMoveActionReturnsFalseWhenMovingOutOfBounds() {
        assertFalse(this.executeMoveAction(DRIVER_AGENT, Direction.NORTH));
    }

    @Test
    @DisplayName("Test that executing a move action returns false when trying to directly move into a parking lot")
    public void testExecutingMoveActionReturnsFalseWhenMovingIntoParkingLot() {
        assertFalse(this.executeMoveAction(DRIVER_AGENT, Direction.WEST));
    }

    @Test
    @DisplayName("Test that executing a move action returns false when the driver is inside a parking lot")
    public void testExecutingMoveActionReturnsFalseWhenDriverIsInsideAParkingLot() {
        assertTrue(this.executeEnterAction(DRIVER_AGENT, PARKING_AGENT));
        assertFalse(this.executeMoveAction(DRIVER_AGENT, Direction.EAST));
    }

    @Test
    @DisplayName("Test that executing a move action fails when the driver is not valid")
    public void testFailWhenExecutingMoveActionWithInvalidDriver() {
        assertThrows(IllegalArgumentException.class, () ->
                this.environment.executeAction(PARKING_AGENT, this.createMoveAction(Direction.EAST)));
    }

    private Structure createEnterAction(final String driver) {
        return ASSyntax.createStructure(ParkingEnvironment.ENTER_ACTION, ASSyntax.createAtom(driver));
    }

    private boolean executeEnterAction(final String driver, final String parking) {
        return this.environment.executeAction(parking, this.createEnterAction(driver));
    }

    @Test
    @DisplayName("Test that executing an enter action updates the driver position correctly")
    public void testExecuteEnterAction() {
        assertTrue(this.executeEnterAction(DRIVER_AGENT, PARKING_AGENT));
        assertEquals(this.getPosition(PARKING_AGENT), this.getPosition(DRIVER_AGENT));
    }

    @Test
    @DisplayName("Test that executing an enter action returns false when the driver is not adjacent to the parking")
    public void testExecutingEnterActionReturnsFalseWhenDriverNotAdjacentToParking() {
        assertFalse(this.executeEnterAction("d2", PARKING_AGENT));
    }

    @Test
    @DisplayName("Test that executing an enter action fails when the agents are not valid")
    public void testFailWhenExecutingEnterActionWithInvalidAgents() {
        assertAll(() -> {
            assertThrows(IllegalArgumentException.class, () -> this.executeEnterAction(PARKING_AGENT, PARKING_AGENT));
            assertThrows(IllegalArgumentException.class, () -> this.executeEnterAction(DRIVER_AGENT, DRIVER_AGENT));
        });
    }

    private Structure createExitAction(final String driver, final Direction direction) {
        return ASSyntax.createStructure(ParkingEnvironment.EXIT_ACTION,
                ASSyntax.createAtom(driver), ASSyntax.createAtom(direction.toString().toLowerCase()));
    }

    private boolean executeExitAction(final String driver, final String parking, final Direction direction) {
        return this.environment.executeAction(parking, this.createExitAction(driver, direction));
    }

    @Test
    @DisplayName("Test that executing an exit action updates the driver position correctly")
    public void testExecuteExitAction() {
        this.executeEnterAction(DRIVER_AGENT, PARKING_AGENT);
        assertTrue(this.executeExitAction(DRIVER_AGENT, PARKING_AGENT, Direction.EAST));
        assertEquals(new Position(this.getPosition(PARKING_AGENT).x(), this.getPosition(PARKING_AGENT).y() + 1),
                this.getPosition(DRIVER_AGENT));
    }

    @Test
    @DisplayName("Test that executing an exit action returns false when the driver is not inside the parking lot")
    public void testExecutingExitActionReturnsFalseWhenNotInsideParkingLot() {
        assertFalse(this.executeExitAction(DRIVER_AGENT, PARKING_AGENT, Direction.EAST));
    }

    @Test
    @DisplayName("Test that executing an exit action returns false when the driver would move out of bounds")
    public void testExecutingExitActionReturnsFalseWhenMovingOutOfBounds() {
        this.executeEnterAction(DRIVER_AGENT, PARKING_AGENT);
        assertFalse(this.executeExitAction(DRIVER_AGENT, PARKING_AGENT, Direction.WEST));
    }

    @Test
    @DisplayName("Test that executing an exit action fails when the agents are not valid")
    public void testFailWhenExecutingExitActionWithInvalidAgents() {
        assertAll(() -> {
            assertThrows(IllegalArgumentException.class, () ->
                    this.executeExitAction(PARKING_AGENT, PARKING_AGENT, Direction.EAST));
            assertThrows(IllegalArgumentException.class, () ->
                    this.executeExitAction(DRIVER_AGENT, DRIVER_AGENT, Direction.EAST));
        });
    }
}
