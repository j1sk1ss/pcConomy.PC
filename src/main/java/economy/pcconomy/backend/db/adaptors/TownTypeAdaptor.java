package economy.pcconomy.backend.db.adaptors;

import com.google.gson.*;
import economy.pcconomy.backend.economy.town.NpcTown;
import economy.pcconomy.backend.economy.town.PlayerTown;
import economy.pcconomy.backend.economy.town.Town;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;


public class TownTypeAdaptor implements JsonSerializer<Town>, JsonDeserializer<Town> {
    @Override
    public JsonElement serialize(Town src, Type typeOfSrc, JsonSerializationContext context) {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", src.getUUID().toString());
        jsonObject.addProperty("budget", src.getBudget());
        // jsonObject.addProperty("credit", src.getCreditList()); TODO: Save credit

        if (src instanceof NpcTown) {
            NpcTown npcTown = (NpcTown)src;
            jsonObject.addProperty("usefulStorage", npcTown.usefulStorage);
            jsonObject.addProperty("usefulBudget", npcTown.usefulBudget);
            jsonObject.addProperty("townVAT", npcTown.townVAT);
            // jsonObject.addProperty("storage", npcTown.Storage); TODO: Save storage
        }

        return jsonObject;
    }

    @Override
    public Town deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        var jsonObject = json.getAsJsonObject();
        var uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
        var budget = jsonObject.get("budget").getAsDouble();

        // TODO: Load credit
        // TODO: Load traders

        if (jsonObject.has("usefulStorage")) {
            var usefulStorage = jsonObject.get("usefulStorage").getAsDouble();
            var usefulBudget = jsonObject.get("usefulBudget").getAsDouble();
            var townVAT = jsonObject.get("townVAT").getAsDouble();

            // TODO: Load storage
            

            return new NpcTown(uuid, new ArrayList<>(), new ArrayList<>(), budget, usefulStorage, usefulBudget, townVAT, new ArrayList<>());
        }

        return new PlayerTown(uuid, new ArrayList<>(), new ArrayList<>());
    }
}
