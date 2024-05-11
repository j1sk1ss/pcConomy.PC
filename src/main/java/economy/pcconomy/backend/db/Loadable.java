package economy.pcconomy.backend.db;

import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Loadable {
    /**
     * Saves loadable into .json file
     * @param path File name (without format)
     * @throws IOException If something goes wrong
     */
    public void save(String path) throws IOException {
        var writer = new FileWriter(path + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(this, writer);

        writer.close();
    }

    /**
     * Loads loadable data from .json
     * @param path File name (without format)
     * @return Bank object
     * @throws IOException If something goes wrong
     */
    public <T extends Loadable> T load(String path, Class<T> target) throws IOException {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(path + ".json"))), target);
    }

    /**
     * Get name of manager`s data
     * @return name
     */
    public abstract String getName();
}
