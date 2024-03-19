package economy.pcconomy.backend.save.adaptors;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import economy.pcconomy.backend.economy.town.NpcTown;
import economy.pcconomy.backend.economy.town.Town;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class TownTypeAdaptor implements JsonSerializer<Town>, JsonDeserializer<Town> {
    @Override
    public JsonElement serialize(Town src, Type typeOfSrc, JsonSerializationContext context) {
        var parameters = new ArrayList<String>();
        parameters.add(src.getClass() == NpcTown.class ? "npc-town" : "player-town");

        parameters.add(src.getUUID().toString()); // Save UUID
        parameters.add(src.getBudget() + ""); // Save budget
        for (var credit : src.getCreditList())
            parameters.add(credit.toString()); // TODO: To string?

        for (var borrower : src.getBorrowers())
            parameters.add(borrower.toString()); // Save borrowers

        return new JsonParser().parse( new Gson().toJson(parameters, new TypeToken<List<String>>(){}.getType()) );
    }

    @Override
    public Town deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return null; // TODO: Return from JSON Town
    }
}
