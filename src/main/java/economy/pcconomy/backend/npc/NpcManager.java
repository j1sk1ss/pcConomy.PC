package economy.pcconomy.backend.npc;

import com.google.gson.GsonBuilder;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.db.ItemStackTypeAdaptor;
import economy.pcconomy.backend.db.Loadable;
import economy.pcconomy.backend.npc.traits.*;
import lombok.experimental.ExtensionMethod;
import economy.pcconomy.backend.economy.town.TownManager;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;


@ExtensionMethod({Cash.class, TownManager.class})
public class NpcManager implements Loadable {
    public final Map<Integer, Trader> Npc = new Hashtable<>();
    public static final double traderCost = PcConomy.Config.getDouble("npc.trader_cost", 1500d);

    /**
     * Create NPC with special trait
     * @param creator Player that create NPC
     * @param trait Trait class
     */
    public static void createNPC(Player creator, Trait trait) {
        var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, trait.getName());
        trait.linkToNPC(npc);
        npc.addTrait(trait);
        npc.spawn(creator.getLocation());
    }

    /**
     * Update list of available NPC
     */
    public static void reloadNPC() {
        // Register traits
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry()) {
            if (PcConomy.GlobalNPC.Npc.get(npc.getId()) != null)
                PcConomy.GlobalNPC.Npc.get(npc.getId()).linkToNPC(npc);

            switch (npc.getName()) {
                case "npcloaner"   -> npc.addTrait(NpcLoaner.class);
                case "banker"      -> npc.addTrait(Banker.class);
                case "licensor"    -> npc.addTrait(Licensor.class);
                case "npctrader"   -> npc.addTrait(NpcTrader.class);
                case "trader"      -> npc.addTrait(Trader.class);
                case "shareholder" -> npc.addTrait(Shareholder.class);
            }
        }
    }

    /**
     * Get NPC class by NPC id
     * @param id ID of NPC
     * @return NPC class
     */
    public static net.citizensnpcs.api.npc.NPC getNPC(int id) {
        return CitizensAPI.getNPCRegistry().getById(id);
    }

    @Override
    public void save(String fileName) throws IOException {
        // Check all server NPC
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry())
            if (npc.hasTrait(Trader.class)) Npc.put(npc.getId(), npc.getOrAddTrait(Trader.class));

        var writer = new FileWriter(fileName + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .toJson(this, writer);

        writer.close();
    }

    @Override
    public NpcManager load(String fileName) throws IOException {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(fileName + ".json"))), NpcManager.class);
    }

    @Override
    public String getName() {
        return "npc_data";
    }
}
