package economy.pcconomy.backend.town.scripts;

import com.google.gson.GsonBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;
import economy.pcconomy.backend.town.objects.TownObject;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TownManager {
    public final List<TownObject> townObjects = new ArrayList<>();

    /***
     * Reload and save all towns from server
     */
    public void reloadTownObjects() {
        townObjects.clear();
        for (com.palmergames.bukkit.towny.object.Town town : TownyAPI.getInstance().getTowns())
            createTownObject(town, false);
    }

    /***
     * Create new town in plugin
     * @param town Created town
     * @param isNPC Is this town belongs NPC
     */
    public void createTownObject(com.palmergames.bukkit.towny.object.Town town, boolean isNPC) {
        townObjects.add(new TownObject(town, isNPC));
    }

    /***
     * Destroys town from plugin
     * @param townName Name of town that was destroyed
     */
    public void destroyTownObject(String townName) {
        for (TownObject townObject : townObjects)
            if (townObject.TownName.equals(townName)) {
                townObjects.remove(townObject);
                break;
            }
    }

    /***
     * Changes town NPS status
     * @param townName Name of town that change status
     * @param isNPC New status
     */
    public void changeNPCStatus(String townName, boolean isNPC) {
        var townObject = getTownObject(townName);
        townObject.isNPC = isNPC;
        townObject.initializeNPC();
        setTownObject(townObject);
    }

    /***
     * Gets town from list of town in plugin
     * @param townName Name of town
     * @return TownObject
     */
    public TownObject getTownObject(String townName) {
        for (TownObject townObject : townObjects)
            if (townObject.TownName.equals(townName))
                return townObject;

        return null;
    }

    /***
     * Sets town to list of town in plugin
     * @param town New townObject
     */
    public void setTownObject(TownObject town) {
        // Обновление обьекта города
        for (TownObject townObject : townObjects)
            if (townObject.TownName.equals(town.TownName)) {
                townObjects.remove(townObject);
                townObjects.add(town);
            }
    }

    /***
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
