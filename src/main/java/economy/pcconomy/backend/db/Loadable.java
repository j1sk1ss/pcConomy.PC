package economy.pcconomy.backend.db;

import java.io.IOException;

public interface Loadable {
    /**
     * Saves loadable into .json file
     * @param path File name (without format)
     * @throws IOException If something goes wrong
     */
    void save(String path) throws IOException;

    /**
     * Loads loadable data from .json
     * @param path File name (without format)
     * @return Bank object
     * @throws IOException If something goes wrong
     */
    Loadable load(String path) throws IOException;

    /**
     * Get name of manager`s data
     * @return name
     */
    String getName();
}
