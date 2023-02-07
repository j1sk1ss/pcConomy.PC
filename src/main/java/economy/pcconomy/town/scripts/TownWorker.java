package economy.pcconomy.town.scripts;

import economy.pcconomy.PcConomy;
import economy.pcconomy.town.objects.TownObject;

import java.util.ArrayList;
import java.util.List;

public class TownWorker {

    public static List<TownObject> townObjects = new ArrayList<>(); // все города сервера

    public static void AddOldTowns() {
        for (com.palmergames.bukkit.towny.object.Town town:
                PcConomy.TownyAPI.getTowns()) {
            CreateTownObject(town, false);
        }
    }

    public static void CreateTownObject(com.palmergames.bukkit.towny.object.Town town, boolean isNPC) {
        // метод который должен быть вызван вместе с созданием города игроком
        townObjects.add(new TownObject(town, isNPC));
    }

    public static void DestroyTownObject(String townName) {
        // метод который должен быть вызван вместе с удалением города игрока
        for (TownObject townObject:
                townObjects) {
            if (townObject.Town.getName().equals(townName)) {
                townObjects.remove(townObject);
                break;
            }
        }
    }

    public static void ChangeNPCStatus(String townName, boolean isNPC) {
        // Метод изменяющий город игрока на город NPC
        var townObject = GetTownObject(townName);
        townObject.isNPC = isNPC;
        SetTownObject(townObject);
    }

    public static TownObject GetTownObject(String townName) {
        // Получение обьекта города
        for (TownObject townObject:
                townObjects) {
            if (townObject.Town.getName().equals(townName)) {
                return townObject;
            }
        }

        return null;
    }

    public static void SetTownObject(TownObject town) {
        // Обновление обьекта города
        for (TownObject townObject:
                townObjects) {
            if (townObject.Town.equals(town.Town)) {
                townObjects.remove(townObject);
                townObjects.add(town);
            }
        }
    }

}
