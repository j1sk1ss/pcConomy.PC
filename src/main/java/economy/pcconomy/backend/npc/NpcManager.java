package economy.pcconomy.backend.npc;

import com.google.gson.GsonBuilder;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.db.ItemStackTypeAdaptor;
import economy.pcconomy.backend.db.Loadable;
import economy.pcconomy.backend.npc.traits.*;
import lombok.experimental.ExtensionMethod;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensDisableEvent;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;


@ExtensionMethod({Cash.class})
public class NpcManager extends Loadable implements Listener {
    public Map<Integer, TraderData> Npc = new Hashtable<>();
    public static final double traderCost = PcConomy.Config.getDouble("npc.trader_cost", 1500d);

    @EventHandler
    public void RegisterNpc(CitizensEnableEvent event) {
        System.out.print("[PcConomy] Traits registering.\n");

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Trader.class).withName("trader"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NpcLoaner.class).withName("npcloaner"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NpcTrader.class).withName("npctrader"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Banker.class).withName("banker"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Licensor.class).withName("licensor"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Shareholder.class).withName("shareholder"));

        System.out.print("[PcConomy] NPC reloading.\n");

        NpcManager.reloadNPC();
    }

    @EventHandler
    public void SaveNpc(CitizensDisableEvent event) {
        try {
            System.out.print("[PcConomy] Traits saving.\n");
            save("plugins\\PcConomy\\" + getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry()) {
            if (PcConomy.GlobalNPC.Npc.get(npc.getId()) != null) {
                var trader = new Trader(PcConomy.GlobalNPC.Npc.get(npc.getId()));
                trader.linkToNPC(npc);
                npc.addTrait(trader);
                continue;
            }

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
        Npc.clear();
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry())
            if (npc.hasTrait(Trader.class)) {
                Npc.put(npc.getId(), new TraderData(npc.getOrAddTrait(Trader.class)));
            }

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
    public <T extends Loadable> T load(String path, Class<T> target) throws IOException {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(path + ".json"))), target);
    }

    @Override
    public String getName() {
        return "npc_data";
    }
}
