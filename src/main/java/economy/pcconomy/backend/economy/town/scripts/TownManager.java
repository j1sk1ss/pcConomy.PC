package economy.pcconomy.backend.economy.town.scripts;

import com.google.gson.GsonBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.backend.economy.town.objects.town.NpcTown;
import economy.pcconomy.backend.economy.town.objects.town.PlayerTown;
import economy.pcconomy.backend.economy.town.objects.town.Town;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TownManager {
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
     * @param townName Name of town that was destroyed
     */
    public void destroyTown(String townName) {
        for (var townObject : towns)
            if (townObject.getName().equals(townName)) {
                towns.remove(townObject);
                break;
            }
    }

    /**
     * Changes town NPS status
     * @param townName Name of town that change status
     * @param isNPC New status
     */
    public void changeNPCStatus(String townName, boolean isNPC) {
        var townObject = getTown(townName);
        townObject = isNPC ? new NpcTown(Objects.requireNonNull(TownyAPI.getInstance().getTown(townObject.getName()))) :
                new PlayerTown(Objects.requireNonNull(TownyAPI.getInstance().getTown(townObject.getName())));

        setTownObject(townObject);
    }

    /**
     * Gets town from list of town in plugin
     * @param townName Name of town
     * @return TownObject
     */
    public Town getTown(String townName) {
        for (var townObject : towns)
            if (townObject.getName().equals(townName))
                return townObject;

        return null;
    }

    /**
     * Sets town to list of town in plugin
     * @param town New townObject
     */
    public void setTownObject(Town town) {
        for (var currentTown : towns)
            if (currentTown.getName().equals(town.getName())) {
                towns.remove(currentTown);
                towns.add(town);
            }
    }

    /**
     * Saves towns into .json file
     * @param fileName File name
     * @throws IOException If something goes wrong
     */
    public void saveTown(String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .toJson(this, writer);
        writer.close();
    }
}
