package parking.view;

import parking.common.Occupier;
import parking.common.Position;

import java.util.Map;
import java.util.Set;

public interface CityView {

    /**
     * Shows or hides the view depending on the value of parameter b.
     *
     * @param b the visibility flag.
     */
    void setVisible(boolean b);

    /**
     * Updates the view of the city.
     *
     * @param city the city represented as a map containing each position and its occupiers.
     */
    void update(Map<Position, Set<Occupier>> city);
}
