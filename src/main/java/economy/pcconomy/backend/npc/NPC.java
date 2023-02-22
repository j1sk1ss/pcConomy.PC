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
    public Map<Integer, TraderObject> Traders = new Hashtable<>(); // Для сохранения

    public void CreateNPC(Player creator, Trait trait) {
        var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, trait.getName());

        npc.addTrait(trait);
        npc.spawn(creator.getLocation());
    }

    public void UpdateNPC() {
        for (net.citizensnpcs.api.npc.NPC npc:
             CitizensAPI.getNPCRegistry()) {
            switch (npc.getName()) {
                case "npcloaner" -> npc.addTrait(NPCLoaner.class);
                case "Loaner" -> npc.addTrait(Loaner.class);
                case "banker" -> npc.addTrait(Banker.class);
                case "licensor" -> npc.addTrait(Licensor.class);
                case "npctrader" -> npc.addTrait(NPCTrader.class);
                case "Trader" -> npc.addTrait(Trader.class);
            }
        }
        LoadTraders();
    }

    public static double traderCost = 3000d;

    public void BuyTrader(Player creator) {
        var cash = new Cash();
        if (cash.AmountOfCashInInventory(creator) < traderCost) return;

        var marketLicense = PcConomy.GlobalLicenseWorker.GetLicense(creator.getUniqueId(), LicenseType.Market);
        if (marketLicense == null) return;
        if (PcConomy.GlobalLicenseWorker.isOverdue(marketLicense)) return;

        cash.TakeCashFromInventory(traderCost, creator);
        PcConomy.GlobalBank.BankBudget += traderCost;

        var trader = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Trader");
        trader.spawn(creator.getLocation());
        trader.addTrait(Trader.class);
    }

    public static double loanerCost = 4500d;

    public void BuyLoaner(Player creator) {
        var cash = new Cash();
        if (cash.AmountOfCashInInventory(creator) < loanerCost) return;

        var loanerLicense = PcConomy.GlobalLicenseWorker.GetLicense(creator.getUniqueId(), LicenseType.Loan);
        if (loanerLicense == null) return;
        if (PcConomy.GlobalLicenseWorker.isOverdue(loanerLicense)) return;

        cash.TakeCashFromInventory(loanerCost, creator);
        PcConomy.GlobalBank.BankBudget += loanerCost;

        var loaner = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Loaner");
        loaner.spawn(creator.getLocation());
        loaner.addTrait(Loaner.class);
    }

    public net.citizensnpcs.api.npc.NPC GetNPC(int id) {
        for (net.citizensnpcs.api.npc.NPC npc:
                CitizensAPI.getNPCRegistry()) {
            if (npc.hasTrait(Trader.class)) {
                if (npc.getId() == id) {
                    return npc;
                }
            }
        }

        return null;
    }

    public void LoadTraders() {
        for (int id:
                Traders.keySet()) {
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

        for (net.citizensnpcs.api.npc.NPC npc:
                CitizensAPI.getNPCRegistry()) {
            if (npc.hasTrait(Trader.class)) {
                var traderTrait = npc.getTrait(Trader.class);

                Traders.put(npc.getId(), new TraderObject(traderTrait.Storage, traderTrait.Revenue, traderTrait.Margin,
                        traderTrait.Cost, traderTrait.isRanted, traderTrait.homeTown, traderTrait.Owner, traderTrait.Term));
            }
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
