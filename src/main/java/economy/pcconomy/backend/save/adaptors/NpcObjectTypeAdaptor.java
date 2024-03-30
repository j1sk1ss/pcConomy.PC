package economy.pcconomy.backend.save.adaptors;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import economy.pcconomy.backend.npc.objects.NpcObject;
import economy.pcconomy.backend.npc.objects.LoanerObject;
import economy.pcconomy.backend.npc.objects.TraderObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


// TODO: Maybe delete this? I moved NPC parent class from interface to default class

public class NpcObjectTypeAdaptor implements JsonSerializer<NpcObject>, JsonDeserializer<NpcObject> {
    @Override
    public JsonElement serialize(NpcObject src, Type typeOfSrc, JsonSerializationContext context) {
        var parameters = new ArrayList<String>();
        parameters.add(src.getClass() == TraderObject.class ? "player-trader" : "player-loaner");

        parameters.add( String.valueOf( src.Revenue ) );
        if (src.HomeTown != null) parameters.add(src.HomeTown.toString());
        else parameters.add("None");

        return new JsonParser().parse( new Gson().toJson(parameters, new TypeToken<List<String>>(){}.getType()) );
    }

    @Override
    public NpcObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        var gson = new Gson();
        var jsonArr = json.getAsJsonArray();
        var desPull = jsonArr.get(1).getAsDouble();

        UUID desHomeTown;
        if (jsonArr.get(2).getAsString().equals("None")) desHomeTown = null;
        else desHomeTown = gson.fromJson(jsonArr.get(2).getAsJsonObject().toString(), UUID.class);

        return new LoanerObject(desPull, desHomeTown);
    }
}
