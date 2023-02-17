package economy.pcconomy.backend.npc;

import com.google.gson.GsonBuilder;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.npc.Banker;
import economy.pcconomy.backend.bank.npc.Loaner;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.license.npc.Licensor;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;
import economy.pcconomy.backend.trade.npc.NPCTrader;
import economy.pcconomy.backend.trade.npc.Trader;

import economy.pcconomy.backend.trade.objects.TraderObject;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;

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
        if (CitizensAPI.getTraitFactory().getTraitClass(trait.getName()) == null)
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Banker.class).withName(trait.getName()));
        var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, trait.getName());

        npc.addTrait(trait);
        npc.spawn(creator.getLocation());
    }

    public void UpdateNPC() {
        for (net.citizensnpcs.api.npc.NPC npc:
             CitizensAPI.getNPCRegistry()) {

            switch (npc.getName()) {
                case "loaner" -> {
                    if (CitizensAPI.getTraitFactory().getTraitClass(npc.getName()) == null)
                        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Loaner.class).withName(npc.getName()));
                    npc.addTrait(Loaner.class);
                }
                case "banker" -> {
                    if (CitizensAPI.getTraitFactory().getTraitClass(npc.getName()) == null)
                        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Banker.class).withName(npc.getName()));
                    npc.addTrait(Banker.class);
                }
                case "licensor" -> {
                    if (CitizensAPI.getTraitFactory().getTraitClass(npc.getName()) == null)
                        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Licensor.class).withName(npc.getName()));
                    npc.addTrait(Licensor.class);
                }
                case "npctrader" -> {
                    if (CitizensAPI.getTraitFactory().getTraitClass(npc.getName()) == null)
                        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NPCTrader.class).withName(npc.getName()));
                    npc.addTrait(NPCTrader.class);
                }
                case "Trader" -> {
                    if (CitizensAPI.getTraitFactory().getTraitClass(npc.getName()) == null)
                        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Trader.class).withName(npc.getName()));
                    npc.addTrait(Trader.class);
                }
            }
        }
        LoadTraders();
    }

    public static double traderCost = 3000d;

    public void BuyTrader(Player creator) {
        var cash = new Cash();
        if (cash.AmountOfCashInInventory(creator) < traderCost) return;

        if (PcConomy.GlobalLicenseWorker.GetLicense(creator, LicenseType.Market) == null) return;
        if (PcConomy.GlobalLicenseWorker.isOverdue(PcConomy.GlobalLicenseWorker.GetLicense(creator, LicenseType.Market))) return;

        cash.TakeCashFromInventory(traderCost, creator);
        PcConomy.GlobalBank.BankBudget += traderCost;

        if (CitizensAPI.getTraitFactory().getTraitClass("Trader") == null)
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Trader.class).withName("Trader"));
        var trader = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Trader");

        trader.spawn(creator.getLocation());
        trader.addTrait(Trader.class);
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
            CitizensAPI.getNPCRegistry().getById(id).getTrait(Trader.class).Owner = Traders.get(id).Owner;
            CitizensAPI.getNPCRegistry().getById(id).getTrait(Trader.class).Storage = Traders.get(id).Storage;
            CitizensAPI.getNPCRegistry().getById(id).getTrait(Trader.class).Revenue = Traders.get(id).Revenue;
            CitizensAPI.getNPCRegistry().getById(id).getTrait(Trader.class).Cost = Traders.get(id).Cost;
            CitizensAPI.getNPCRegistry().getById(id).getTrait(Trader.class).Margin = Traders.get(id).Margin;
            CitizensAPI.getNPCRegistry().getById(id).getTrait(Trader.class).homeTown = Traders.get(id).homeTown;
            CitizensAPI.getNPCRegistry().getById(id).getTrait(Trader.class).isRanted = Traders.get(id).isRanted;
            CitizensAPI.getNPCRegistry().getById(id).getTrait(Trader.class).Term = Traders.get(id).Term;
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
