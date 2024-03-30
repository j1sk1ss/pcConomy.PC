package economy.pcconomy.backend.economy.town.scripts;

import com.google.gson.GsonBuilder;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.backend.economy.town.NpcTown;
import economy.pcconomy.backend.economy.town.PlayerTown;
import economy.pcconomy.backend.economy.town.Town;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;

import economy.pcconomy.backend.save.adaptors.TownTypeAdaptor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class TownManager {
    // TODO: Gson can`t save abstract classes, but we have npc and non npc towns, that`s why i think we need to save them separately
    // P.S. Don`t just delete abstraction, it will lead to junk-coding :)
    public final List<Town> towns = new ArrayList<>();

    /**
     * Reload and save all towns from server
     */
    public void reloadTownObjects() {
        towns.clear();
        for (com.palmergames.bukkit.towny.object.Town town : TownyAPI.getInstance().getTowns())
            createTownObject(town, false);
    }

    /**
     * Create new town in plugin
     * @param town Created town
     * @param isNPC Is this town belongs NPC
     */
    public void createTownObject(com.palmergames.bukkit.towny.object.Town town, boolean isNPC) {
        towns.add(isNPC ? new NpcTown(town) : new PlayerTown(town));
    }

    /**
     * Destroys town from plugin
     * @param townUUID UUID of town that was destroyed
     */
    public void destroyTown(UUID townUUID) {
        for (var townObject : towns)
            if (townObject.getUUID().equals(townUUID)) {
                towns.remove(townObject);
                break;
            }
    }

    /**
     * Changes town NPS status
     * @param townUUID Name of town that change status
     * @param isNPC New status
     */
    public void changeNPCStatus(UUID townUUID, boolean isNPC) {
        var townObject = getTown(townUUID);
        setTownObject(isNPC ?
                new NpcTown(Objects.requireNonNull(TownyAPI.getInstance().getTown(townObject.getUUID()))) :
                new PlayerTown(Objects.requireNonNull(TownyAPI.getInstance().getTown(townObject.getUUID()))));
    }

    /**
     * Gets town from list of town in plugin
     * @param townUUID Name of town
     * @return TownObject
     */
    public Town getTown(UUID townUUID) {
        for (var townObject : towns)
            if (townObject.getUUID().equals(townUUID))
                return townObject;

        return null;
    }

    /**
     * Sets town to list of town in plugin
     * @param town New townObject
     */
    public void setTownObject(Town town) {
        for (var currentTown : towns)
            if (currentTown.getUUID().equals(town.getUUID())) {
                towns.remove(currentTown);
                towns.add(town);
            }
    }

    /**
     * Get town prefix
     * @param town Town
     * @return Prefix
     */
    public String getTownPrefix(UUID town) {
        return Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getPrefix();
    }

    /**
     * Saves towns into .json file
     * @param fileName File name
     * @throws IOException If something goes wrong
     */
    public void saveTown(String fileName) throws IOException {
        var writer = new FileWriter(fileName + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .registerTypeHierarchyAdapter(Town.class, new TownTypeAdaptor())
                .create()
                .toJson(this, writer);
        writer.close();
    }
}