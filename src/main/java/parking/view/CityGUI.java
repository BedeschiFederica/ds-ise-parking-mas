package parking.view;

import parking.Occupier;
import parking.OccupierType.*;
import parking.Position;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CityGUI extends JFrame implements CityView {

    private static final Color PARKING_COLOR = new Color(255, 0, 0);

    private final Random random = new Random();
    private final Map<Position, JButton> grid = new HashMap<>();
    private final Map<String, Color> areaColors = new HashMap<>();
    private final Map<String, Color> driverColors = new HashMap<>();

    public CityGUI(final int width, final int height) {
        UIManager.put("Button.disabledText", Color.BLACK);
        final JPanel gridPanel = new JPanel(new GridLayout(height, width));
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                final JButton element = new JButton("");
                element.setEnabled(false);
                element.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridPanel.add(element);
                this.grid.put(new Position(x, y), element);
            }
        }
        this.setContentPane(gridPanel);
        this.setMinimumSize(new Dimension(400, 400));
        this.pack();
    }

    @Override
    public void update(final Map<Position, Occupier> city) {
        city.forEach((position, occupier) -> {
            this.requireCellExistence(position);
            final JButton cell = this.grid.get(position);
            switch (occupier.type()) {
                case AREA:
                    cell.setText("");
                    cell.setBackground(this.getAreaColor(occupier.id()));
                    break;
                case PARKING:
                    cell.setText(occupier.id());
                    cell.setBackground(PARKING_COLOR);
                    break;
                case DRIVER:
                    cell.setText(occupier.id());
                    cell.setBackground(this.getDriverColor(occupier.id()));
                    break;
            }
        });
        this.repaint();
    }

    private void requireCellExistence(final Position position) {
        if (!this.grid.containsKey(position)) {
            throw new IllegalArgumentException("Cell at position " + position + " does not exist");
        }
    }

    private Color getAreaColor(final String id) {
        if (!this.areaColors.containsKey(id)) {
            this.areaColors.put(id, Color.getHSBColor(this.random.nextFloat(), 0.15f, 0.9f));
        }
        return this.areaColors.get(id);
    }

    private Color getDriverColor(final String id) {
        if (!this.driverColors.containsKey(id)) {
            this.driverColors.put(id, Color.getHSBColor(this.random.nextFloat(), 0.7f, 0.85f));
        }
        return this.driverColors.get(id);
    }
}
