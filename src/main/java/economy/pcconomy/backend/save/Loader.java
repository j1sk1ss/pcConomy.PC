package economy.pcconomy.backend.save;

import com.google.gson.GsonBuilder;
import economy.pcconomy.backend.economy.bank.Bank;
import economy.pcconomy.backend.economy.credit.scripts.BorrowerManager;
import economy.pcconomy.backend.license.scripts.LicenseManager;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;
import economy.pcconomy.backend.economy.town.scripts.TownManager;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Loader {
    /***
     * Loads bank data from .json
     * @param fileName File name (without format)
     * @return Bank object
     * @throws IOException If something goes wrong
     */
    public static Bank loadBank(String fileName) throws IOException {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(fileName + ".json"))), Bank.class);
    }

    /***
     * Loads NPC data from .json
     * @param fileName File name (without format)
     * @return NPC object
     * @throws IOException If something goes wrong
     */
    public static NpcManager loadNPC(String fileName) throws IOException { // TODO: Adaptor or something like this. Saving don`t work correctly
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(fileName + ".json"))), NpcManager.class);
    }

    /***
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

    /***
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
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(fileName + ".json"))), TownManager.class);
    }

    /***
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
}


