package parking.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import parking.common.Position;

public class CityLoaderImpl implements CityLoader {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public City load(final String jsonFile) {
        try (InputStream input = CityLoaderImpl.class.getClassLoader().getResourceAsStream(jsonFile)) {
            if (input == null) {
                throw new IllegalArgumentException("City configuration not found: " + jsonFile);
            }
            return this.mapper.readValue(input, CityJson.class).toCity();
        } catch (final IOException e) {
            throw new RuntimeException("Unable to load city configuration: " + jsonFile, e);
        }
    }

    private record CityJson(int width, int height, List<AreaJson> areas, List<ParkingLotJson> parkingLots,
                            List<DriverJson> drivers) {
        public City toCity() {
            return new CityImpl(
                    this.width,
                    this.height,
                    this.areas.stream().map(a -> new Area(a.id, new Position(a.x1, a.y1), new Position(a.x2, a.y2)))
                            .toList(),
                    this.parkingLots.stream().collect(Collectors.toMap(
                            p -> new ParkingLotId(p.id),
                            p -> new Position(p.x, p.y)
                    )),
                    this.drivers.stream().collect(Collectors.toMap(
                            d -> new DriverId(d.id),
                            d -> new Position(d.x, d.y)
                    ))
            );
        }
    }

    private record AreaJson(String id, int x1, int y1, int x2, int y2) {}
    private record ParkingLotJson(String id, int x, int y) {}
    private record DriverJson(String id, int x, int y) {}
}
