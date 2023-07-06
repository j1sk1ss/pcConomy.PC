package economy.pcconomy.backend.npc;

import com.google.gson.GsonBuilder;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.npc.traits.Banker;
import economy.pcconomy.backend.npc.traits.Loaner;
import economy.pcconomy.backend.npc.traits.NpcLoaner;
import economy.pcconomy.backend.npc.traits.Licensor;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;
import economy.pcconomy.backend.npc.traits.NpcTrader;
import economy.pcconomy.backend.npc.traits.Trader;

import economy.pcconomy.backend.trade.TraderObject;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class NpcManager {
    public final Map<Integer, TraderObject> Traders = new Hashtable<>();

    /***
     * Create NPC with special trait
     * @param creator Player that create NPC
     * @param trait Trait class
     */
    public void createNPC(Player creator, Trait trait) {
        var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, trait.getName());

        npc.addTrait(trait);
        npc.spawn(creator.getLocation());
    }

    public static final double traderCost = PcConomy.Config.getDouble("npc.trader_cost", 1500d);
    public static final double loanerCost = PcConomy.Config.getDouble("npc.loaner_cost", 2000d);

    /***
     * Buy NPC
     * @param buyer Player that buy NPC
     * @param neededLicense License that needs for this
     * @param price Price of NPC
     */
    public void buyNPC(Player buyer, LicenseType neededLicense, double price) {
        var cash = new CashManager();
        if (cash.amountOfCashInInventory(buyer) < price) return;

        var license = PcConomy.GlobalLicenseManager.getLicense(buyer.getUniqueId(), neededLicense);
        if (license == null) return;
        if (license.isOverdue()) return;

        cash.takeCashFromInventory(price, buyer);
        PcConomy.GlobalBank.BankBudget += price;

        var npcList = Map.of(
                LicenseType.Market, new Trader(),
                LicenseType.Loan, new Loaner()
        );

        var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, npcList.get(neededLicense).getName());
        npc.spawn(buyer.getLocation());
        npc.addTrait(npcList.get(neededLicense));
    }

    /***
     * Update list of available NPC
     */
    public void updateNPC() {
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry()) {
            switch (npc.getName()) {
                case "npcloaner" -> npc.addTrait(NpcLoaner.class);
                case "loaner"    -> npc.addTrait(Loaner.class);
                case "banker"    -> npc.addTrait(Banker.class);
                case "licensor"  -> npc.addTrait(Licensor.class);
                case "npctrader" -> npc.addTrait(NpcTrader.class);
                case "trader"    -> npc.addTrait(Trader.class);
            }
        }

        loadTraders();
    }

    /***
     * Get NPC class by NPC id
     * @param id ID of NPC
     * @return NPC class
     */
    public net.citizensnpcs.api.npc.NPC getNPC(int id) {
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry())
            if (npc.hasTrait(Trader.class))
                if (npc.getId() == id)
                    return npc;

        return null;
    }

    /***
     * Load traders and their stuff
     */
    public void loadTraders() {
        for (int id: Traders.keySet()) {
            var trait = new Trader();
            var saveTrait = Traders.get(id);

            trait.Owner    = saveTrait.Owner;
            trait.Storage  = saveTrait.Storage;
            trait.Revenue  = saveTrait.Revenue;
            trait.Cost     = saveTrait.Cost;
            trait.Margin   = saveTrait.Margin;
            trait.homeTown = saveTrait.homeTown;
            trait.isRanted = saveTrait.isRanted;
            trait.Term     = saveTrait.Term;

            CitizensAPI.getNPCRegistry().getById(id).addTrait(trait);
        }
    }

    /***
     * Saves traders list into .json file
     * @param fileName File name
     * @throws IOException If something goes wrong
     */
    public void saveNPC(String fileName) throws IOException {
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry())
            if (npc.hasTrait(Trader.class)) {
                var traderTrait = npc.getOrAddTrait(Trader.class);

                Traders.put(npc.getId(), new TraderObject(traderTrait.Storage, traderTrait.Revenue, traderTrait.Margin,
                        traderTrait.Cost, traderTrait.isRanted, traderTrait.homeTown, traderTrait.Owner, traderTrait.Term));
            }

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
