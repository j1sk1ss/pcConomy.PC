package economy.pcconomy.backend.save;

import com.google.gson.GsonBuilder;
import economy.pcconomy.backend.bank.Bank;
import economy.pcconomy.backend.bank.scripts.BorrowerManager;
import economy.pcconomy.backend.license.scripts.LicenseWorker;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;
import economy.pcconomy.backend.town.scripts.TownWorker;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Loader {

    public static Bank LoadBank(String fileName) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(fileName + ".txt")));

        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(json, Bank.class);
    }

    public static NPC LoadNPC(String fileName) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(fileName + ".txt")));

        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .fromJson(json, NPC.class);
    }

    public static BorrowerManager LoadBorrowers(String fileName) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(fileName + ".txt")));

        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(json, BorrowerManager.class);
    }

    public static TownWorker LoadTowns(String fileName) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(fileName + ".txt")));

        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .fromJson(json, TownWorker.class);
    }

    public static LicenseWorker LoadLicenses(String fileName) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(fileName + ".txt")));

        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(json, LicenseWorker.class);
    }
}


