package economy.pcconomy.backend.economy.town.manager;

import com.google.gson.GsonBuilder;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.backend.economy.town.NpcTown;
import economy.pcconomy.backend.economy.town.PlayerTown;
import economy.pcconomy.backend.economy.town.Town;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.db.ItemStackTypeAdaptor;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class TownManager {
    public final List<Town> Towns = new ArrayList<>();

    /**
     * Reload and save all Towns from server
     */
    public static void reloadTownObjects() {
        PcConomy.GlobalTownManager.Towns.clear();
        for (com.palmergames.bukkit.towny.object.Town town : TownyAPI.getInstance().getTowns())
            createTownObject(town, false);
    }

    /**
     * Create new town in plugin
     * @param town Created town
     * @param isNPC Is this town belongs NPC
     */
    public static void createTownObject(com.palmergames.bukkit.towny.object.Town town, boolean isNPC) {
        PcConomy.GlobalTownManager.Towns.add(isNPC ? new NpcTown(town) : new PlayerTown(town));
    }

    /**
     * Destroys town from plugin
     * @param townUUID UUID of town that was destroyed
     */
    public static void destroyTown(UUID townUUID) {
        for (var townObject : PcConomy.GlobalTownManager.Towns)
            if (townObject.getUUID().equals(townUUID)) {
                PcConomy.GlobalTownManager.Towns.remove(townObject);
                break;
            }
    }

    /**
     * Changes town NPS status
     * @param townUUID UUID of town that change status
     * @param isNPC New status
     */
    public static void changeNPCStatus(UUID townUUID, boolean isNPC) {
        var townObject = getTown(townUUID);
        setTownObject(isNPC ?
                new NpcTown(Objects.requireNonNull(TownyAPI.getInstance().getTown(townObject.getUUID()))) :
                new PlayerTown(Objects.requireNonNull(TownyAPI.getInstance().getTown(townObject.getUUID()))));
    }

    /**
     * Changes town NPS status
     * @param town UUID of town that change status
     * @param isNPC New status
     */
    public static void changeNPCStatus(com.palmergames.bukkit.towny.object.Town town, boolean isNPC) {
        var townObject = getTown(town.getUUID());
        setTownObject(isNPC ?
                new NpcTown(Objects.requireNonNull(TownyAPI.getInstance().getTown(townObject.getUUID()))) :
                new PlayerTown(Objects.requireNonNull(TownyAPI.getInstance().getTown(townObject.getUUID()))));
    }

    /**
     * Gets town from list of town in plugin
     * @param uuid Name of town
     * @return TownObject
     */
    public static Town getTown(UUID uuid) {
        for (var townObject : PcConomy.GlobalTownManager.Towns)
            if (townObject.getUUID().equals(uuid))
                return townObject;

        return null;
    }

    /**
     * Gets town from list of town in plugin
     * @param town Name of town
     * @return TownObject
     */
    public static Town getTown(com.palmergames.bukkit.towny.object.Town town) {
        for (var townObject : PcConomy.GlobalTownManager.Towns)
            if (townObject.getUUID().equals(town.getUUID()))
                return townObject;

        return null;
    }

    /**
     * Sets town to list of town in plugin
     * @param town New townObject
     */
    public static void setTownObject(Town town) {
        for (var currentTown : PcConomy.GlobalTownManager.Towns)
            if (currentTown.getUUID().equals(town.getUUID())) {
                PcConomy.GlobalTownManager.Towns.remove(currentTown);
                PcConomy.GlobalTownManager.Towns.add(town);
            }
    }

    /**
     * Get town prefix
     * @param town Town
     * @return Prefix
     */
    public static String getTownPrefix(UUID town) {
        return Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getPrefix();
    }

    /**
     * Saves Towns into .json file
     * @param fileName File name
     * @throws IOException If something goes wrong
     */
    public void saveTown(String fileName) throws IOException {
        var writer = new FileWriter(fileName + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .toJson(this, writer);

        writer.close();
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
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(fileName + ".json"))), TownManager.class);
    }
}