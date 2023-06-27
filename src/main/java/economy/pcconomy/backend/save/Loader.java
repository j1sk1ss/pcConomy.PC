package economy.pcconomy.backend.save;

import com.google.gson.GsonBuilder;
import economy.pcconomy.backend.bank.Bank;
import economy.pcconomy.backend.bank.scripts.BorrowerManager;
import economy.pcconomy.backend.license.scripts.LicenseManager;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;
import economy.pcconomy.backend.town.scripts.TownManager;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Loader {
    public static Bank loadBank(String fileName) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(fileName + ".json")));

        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(json, Bank.class);
    }

    public static NPC loadNPC(String fileName) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(fileName + ".json")));

        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .fromJson(json, NPC.class);
    }

    public static BorrowerManager loadBorrowers(String fileName) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(fileName + ".json")));

        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(json, BorrowerManager.class);
    }

    public static TownManager loadTowns(String fileName) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(fileName + ".json")));

        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .fromJson(json, TownManager.class);
    }

    public static LicenseManager loadLicenses(String fileName) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(fileName + ".json")));

        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(json, LicenseManager.class);
    }
}


