package economy.pcconomy.backend.save;

import com.google.gson.GsonBuilder;
import economy.pcconomy.backend.economy.credit.scripts.BorrowerManager;
import economy.pcconomy.backend.economy.share.ShareManager;
import economy.pcconomy.backend.economy.town.Town;
import economy.pcconomy.backend.license.scripts.LicenseManager;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;
import economy.pcconomy.backend.economy.town.scripts.TownManager;
import economy.pcconomy.backend.save.adaptors.TownTypeAdaptor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Loader {

    /**
     * Loads NPC data from .json
     * @param fileName File name (without format)
     * @return NPC object
     * @throws IOException If something goes wrong
     */
    public static NpcManager loadNPC(String fileName) throws IOException { // TODO: Check NPC adaptor
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                // .registerTypeHierarchyAdapter(NpcObject.class, new NpcObjectTypeAdaptor())
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(fileName + ".json"))), NpcManager.class);
    }

    /**
     * Loads borrowers data from .json
     * @param fileName File name (without format)
     * @return Borrowers manager object
     * @throws IOException If something goes wrong
     */
    public static BorrowerManager loadBorrowers(String fileName) throws IOException {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(fileName + ".json"))), BorrowerManager.class);
    }

    /**
     * Loads towns data from .json
     * @param fileName File name (without format)
     * @return Town manager object
     * @throws IOException If something goes wrong
     */
    public static TownManager loadTowns(String fileName) throws IOException {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .registerTypeHierarchyAdapter(Town.class, new TownTypeAdaptor())
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(fileName + ".json"))), TownManager.class);
    }

    /**
     * Loads license data from .json
     * @param fileName File name (without format)
     * @return License manager object
     * @throws IOException If something goes wrong
     */
    public static LicenseManager loadLicenses(String fileName) throws IOException {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(fileName + ".json"))), LicenseManager.class);
    }

    /**
     * Loads shares data from .json
     * @param fileName File name (without format)
     * @return License manager object
     * @throws IOException If something goes wrong
     */
    public static ShareManager loadShares(String fileName) throws IOException {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(fileName + ".json"))), ShareManager.class);
    }
}


