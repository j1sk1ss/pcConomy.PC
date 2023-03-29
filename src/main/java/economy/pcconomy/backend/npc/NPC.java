package economy.pcconomy.backend.npc;

import com.google.gson.GsonBuilder;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.npc.Banker;
import economy.pcconomy.backend.bank.npc.Loaner;
import economy.pcconomy.backend.bank.npc.NPCLoaner;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.license.npc.Licensor;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;
import economy.pcconomy.backend.trade.npc.NPCTrader;
import economy.pcconomy.backend.trade.npc.Trader;

import economy.pcconomy.backend.trade.objects.TraderObject;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class NPC {
    public final Map<Integer, TraderObject> Traders = new Hashtable<>(); // Для сохранения

    public void CreateNPC(Player creator, Trait trait) {
        var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, trait.getName());

        npc.addTrait(trait);
        npc.spawn(creator.getLocation());
    }

    public static double traderCost = PcConomy.Config.getDouble("npc.trader_cost", 1500d);
    public static double loanerCost = PcConomy.Config.getDouble("npc.loaner_cost", 2000d);

    private final Map<LicenseType, Trait> npcList = Map.of(
            LicenseType.Market, new Trader(),
            LicenseType.Loan, new Loaner()
    );

    public void BuyNPC(Player buyer, LicenseType neededLicense, double price) {
        var cash = new Cash();
        if (cash.AmountOfCashInInventory(buyer) < price) return;

        if (PcConomy.GlobalLicenseWorker.isOverdue(
                PcConomy.GlobalLicenseWorker.GetLicense(buyer.getUniqueId(), neededLicense))) return;

        cash.TakeCashFromInventory(price, buyer);
        PcConomy.GlobalBank.BankBudget += price;

        var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, npcList.get(neededLicense).getName());
        npc.spawn(buyer.getLocation());
        npc.addTrait(npcList.get(neededLicense));
    }

    public void UpdateNPC() {
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry()) {
            switch (npc.getName()) {
                case "npcloaner" -> npc.addTrait(NPCLoaner.class);
                case "loaner"    -> npc.addTrait(Loaner.class);
                case "banker"    -> npc.addTrait(Banker.class);
                case "licensor"  -> npc.addTrait(Licensor.class);
                case "npctrader" -> npc.addTrait(NPCTrader.class);
                case "trader"    -> npc.addTrait(Trader.class);
            }
        }

        LoadTraders();
    }

    public net.citizensnpcs.api.npc.NPC GetNPC(int id) {
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry())
            if (npc.hasTrait(Trader.class))
                if (npc.getId() == id)
                    return npc;

        return null;
    }

    public void LoadTraders() {
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

    public void SaveNPC(String fileName) throws IOException {
        for (net.citizensnpcs.api.npc.NPC npc: CitizensAPI.getNPCRegistry())
            if (npc.hasTrait(Trader.class)) {
                var traderTrait = npc.getTrait(Trader.class);

                Traders.put(npc.getId(), new TraderObject(traderTrait.Storage, traderTrait.Revenue, traderTrait.Margin,
                        traderTrait.Cost, traderTrait.isRanted, traderTrait.homeTown, traderTrait.Owner, traderTrait.Term));
            }

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
