package parking.view;

import parking.common.Occupier;
import parking.common.OccupierType;
import parking.common.Position;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class CityGUI extends JFrame implements CityView {

    private static final Color PARKING_COLOR = new Color(255, 0, 0);
    private static final Color MULTIPLE_DRIVERS_COLOR = new Color(128, 128, 128);

    private final Random random = new Random();
    private final Map<Position, JButton> grid = new HashMap<>();
    private final Map<String, Color> areaColors = new HashMap<>();
    private final Map<String, Color> driverColors = new HashMap<>();
    private final Map<String, JLabel> parkingLabels = new HashMap<>();

    public CityGUI(final int width, final int height) {
        UIManager.put("Button.disabledText", Color.BLACK);
        final JPanel gridPanel = new JPanel(new GridLayout(height, width));
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                final JButton cell = new JButton("");
                cell.setEnabled(false);
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridPanel.add(cell);
                this.grid.put(new Position(x, y), cell);
            }
        }
        this.setContentPane(gridPanel);
        this.setMinimumSize(new Dimension(400, 400));
        this.pack();
    }

    @Override
    public void update(final Map<Position, Set<Occupier>> city) {
        SwingUtilities.invokeLater(() -> {
            city.forEach((position, occupiers) -> {
                this.requireCellExistence(position);
                final JButton cell = this.grid.get(position);
                switch (this.getType(occupiers)) {
                    case AREA:
                        cell.setText("");
                        cell.setBackground(this.getAreaColor(occupiers.stream().toList().getFirst().id()));
                        break;
                    case PARKING:
                        final String parkingId = this.getParkingId(occupiers);
                        cell.setText(parkingId);
                        cell.setBackground(PARKING_COLOR);
                        this.initListenerIfNecessary(cell, parkingId);
                        this.parkingLabels.get(parkingId).setText("Drivers: " + this.getDriverIds(occupiers));
                        break;
                    case DRIVER:
                        cell.setText(occupiers.size() == 1
                                ? occupiers.stream().toList().getFirst().id()
                                : occupiers.stream().map(Occupier::id).toList().toString());
                        cell.setBackground(occupiers.size() == 1
                                ? this.getDriverColor(occupiers.stream().toList().getFirst().id())
                                : MULTIPLE_DRIVERS_COLOR);
                        break;
                }
            });
            this.repaint();
        });
    }

    private void requireCellExistence(final Position position) {
        if (!this.grid.containsKey(position)) {
            throw new IllegalArgumentException("Cell at position " + position + " does not exist");
        }
    }

    private OccupierType getType(final Set<Occupier> occupiers) {
        if (occupiers.isEmpty()) {
            throw new IllegalArgumentException("Occupiers set cannot be empty");
        }
        if (occupiers.stream().anyMatch(occupier -> occupier.type() == OccupierType.PARKING)) {
            return OccupierType.PARKING;
        } else if (occupiers.stream().anyMatch(occupier -> occupier.type() == OccupierType.DRIVER)) {
            return OccupierType.DRIVER;
        }
        return OccupierType.AREA;
    }

    private String getParkingId(final Set<Occupier> occupiers) {
        return occupiers.stream()
                .filter(occupier -> occupier.type() == OccupierType.PARKING)
                .findFirst()
                .map(Occupier::id)
                .orElseThrow(() -> new IllegalStateException("Parking id not found"));
    }

    private Set<String> getDriverIds(final Set<Occupier> occupiers) {
        return occupiers.stream()
                .filter(occupier -> occupier.type() == OccupierType.DRIVER)
                .map(Occupier::id)
                .collect(Collectors.toSet());
    }

    private void initListenerIfNecessary(final JButton cell, final String parkingId) {
        if (cell.getActionListeners().length == 0) {
            cell.setEnabled(true);
            final JPopupMenu popup = new JPopupMenu();
            final JLabel label = new JLabel("");
            this.parkingLabels.put(parkingId, label);
            popup.add(label);
            cell.addActionListener(e -> popup.show(cell, 0, cell.getHeight()));
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
