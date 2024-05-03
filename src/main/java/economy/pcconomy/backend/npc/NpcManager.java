package economy.pcconomy.backend.npc;

import com.google.gson.GsonBuilder;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.npc.objects.NpcObject;
import economy.pcconomy.backend.npc.traits.*;
import lombok.experimental.ExtensionMethod;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.db.adaptors.ItemStackTypeAdaptor;
import economy.pcconomy.backend.npc.objects.TraderObject;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;


@ExtensionMethod({CashManager.class})
public class NpcManager {
    public final Map<Integer, NpcObject> Npc = new Hashtable<>();
    public static final double traderCost = PcConomy.Config.getDouble("npc.trader_cost", 1500d);

    /**
     * Create NPC with special trait
     * @param creator Player that create NPC
     * @param trait Trait class
     */
    public void createNPC(Player creator, Trait trait) {
        var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, trait.getName());

        trait.linkToNPC(npc);
        npc.addTrait(trait);
        npc.spawn(creator.getLocation());
    }

    /**
     * Buy NPC
     * @param buyer Player that buy NPC
     * @param neededLicense License that needs for this
     * @param price Price of NPC
     */
    public void buyNPC(Player buyer, LicenseType neededLicense, double price) {
        if (buyer.amountOfCashInInventory(false) < price) return;

        var license = PcConomy.GlobalLicenseManager.getLicense(buyer.getUniqueId(), neededLicense);
        if (license == null) return;
        if (license.isOverdue()) return;

        buyer.takeCashFromPlayer(price, false);
        PcConomy.GlobalBank.BankBudget += price;

        var npcList = Map.of(
            LicenseType.Market, new Trader()
        );

        var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, npcList.get(neededLicense).getName());
        npcList.get(neededLicense).linkToNPC(npc);
        npc.spawn(buyer.getLocation());
        npc.addTrait(npcList.get(neededLicense));

        PcConomy.GlobalTownManager.getTown(TownyAPI.getInstance().getTownUUID(buyer.getLocation())).traders.add(npc.getId());
        buyer.sendMessage("Торговец куплен");
    }

    /**
     * Update list of available NPC
     */
    public void reloadNPC() {
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry()) {
            switch (npc.getName()) {
                case "npcloaner"   -> npc.addTrait(NpcLoaner.class);
                case "banker"      -> npc.addTrait(Banker.class);
                case "licensor"    -> npc.addTrait(Licensor.class);
                case "npctrader"   -> npc.addTrait(NpcTrader.class);
                case "trader"      -> npc.addTrait(Trader.class);
                case "shareholder" -> npc.addTrait(Shareholder.class);
            }
        }

        loadNpc();
    }

    /**
     * Get NPC class by NPC id
     * @param id ID of NPC
     * @return NPC class
     */
    public net.citizensnpcs.api.npc.NPC getNPC(int id) {
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry())
            if (npc.hasTrait(Trader.class))
                if (npc.getId() == id) return npc;

        return null;
    }

    /**
     * Load traders and their stuff
     */
    public void loadNpc() {
        for (var id : Npc.keySet()) {
            if (Npc.get(id) instanceof TraderObject traderObject) {
                var npc = CitizensAPI.getNPCRegistry().getById(id);
                var trait = new Trader(traderObject);

                trait.linkToNPC(npc);
                npc.addTrait(trait);
            }
        }
    }

    /**
     * Saves traders list into .json file
     * @param fileName File name
     * @throws IOException If something goes wrong
     */
    public void saveNPC(String fileName) throws IOException {
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry())
            if (npc.hasTrait(Trader.class)) Npc.put(npc.getId(), new TraderObject(npc.getOrAddTrait(Trader.class)));

        var writer = new FileWriter(fileName + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .toJson(this, writer);
        writer.close();
    }
}
