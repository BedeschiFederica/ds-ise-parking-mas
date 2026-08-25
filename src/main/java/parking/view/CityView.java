package parking.view;

import parking.common.Occupier;
import parking.common.Position;

import java.util.Map;

public interface CityView {

    /**
     * Shows or hides the view depending on the value of parameter b.
     *
     * @param b the visibility flag.
     */
    void setVisible(boolean b);

    /**
     * Updates the city.
     *
     * @param city the map of positions to occupiers representing the city.
     */
    void update(Map<Position, Occupier> city);
}
