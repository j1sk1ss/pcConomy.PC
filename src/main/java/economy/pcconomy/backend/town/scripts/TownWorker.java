package economy.pcconomy.backend.town.scripts;

import com.google.gson.GsonBuilder;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;
import economy.pcconomy.backend.town.objects.TownObject;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TownWorker {
    public List<TownObject> townObjects = new ArrayList<>(); // все города сервера

    public void AddOldTowns() {
        for (com.palmergames.bukkit.towny.object.Town town:
                PcConomy.TownyAPI.getTowns()) {
            CreateTownObject(town, false);
        }
    }

    public void CreateTownObject(com.palmergames.bukkit.towny.object.Town town, boolean isNPC) {
        // метод который должен быть вызван вместе с созданием города игроком
        townObjects.add(new TownObject(town, isNPC));
    }

    public void DestroyTownObject(String townName) {
        // метод который должен быть вызван вместе с удалением города игрока
        for (TownObject townObject:
                townObjects) {
            if (townObject.TownName.equals(townName)) {
                townObjects.remove(townObject);
                break;
            }
        }
    }

    public void ChangeNPCStatus(String townName, boolean isNPC) {
        // Метод изменяющий город игрока на город NPC
        var townObject = GetTownObject(townName);
        townObject.isNPC = isNPC;
        townObject.InitializeNPC();
        SetTownObject(townObject);
    }

    public TownObject GetTownObject(String townName) {
        // Получение обьекта города
        for (TownObject townObject:
                townObjects) {
            if (townObject.TownName.equals(townName)) {
                return townObject;
            }
        }

        return null;
    }

    public void SetTownObject(TownObject town) {
        // Обновление обьекта города
        for (TownObject townObject:
                townObjects) {
            if (townObject.TownName.equals(town.TownName)) {
                townObjects.remove(townObject);
                townObjects.add(town);
            }
        }
    }

    public void SaveTown(String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName + ".txt", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .toJson(this, writer);
        writer.close();
    }
}
