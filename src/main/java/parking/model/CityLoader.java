package parking.model;

public interface CityLoader {

    /**
     * Loads a city configuration from the specified JSON file.
     *
     * @param jsonFile the JSON file from which to load the city configuration
     * @return the loaded city configuration.
     */
    City load(final String jsonFile);
}
