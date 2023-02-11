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

import java.util.Random;

public class NPC {
    public Map<Integer, Trait> Traits = new Hashtable<>(); // Для сохранения

    public void CreateBanker(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Banker.class).withName(new Random().nextInt() + ""));

        var banker = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Banker");

        banker.addTrait(Banker.class);
        Traits.put(banker.getId(), banker.getTrait(Banker.class));
        banker.spawn(creator.getLocation());
    }

    public void CreateLoaner(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Loaner.class).withName(new Random().nextInt() + ""));

        var loaner = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Loaner");

        loaner.addTrait(Loaner.class);
        Traits.put(loaner.getId(), loaner.getTrait(Loaner.class));
        loaner.spawn(creator.getLocation());
    }

    public void CreateTrader(Player creator) {
        var cash = new Cash();
        double traderCost = 3000d;
        if (cash.AmountOfCashInInventory(creator) < traderCost) return;

        if (PcConomy.GlobalLicenseWorker.GetLicense(creator, LicenseType.Market) == null) return;
        if (PcConomy.GlobalLicenseWorker.isOverdue(PcConomy.GlobalLicenseWorker.GetLicense(creator, LicenseType.Market))) return;

        cash.TakeCashFromInventory(traderCost, creator);
        PcConomy.GlobalBank.BankBudget += traderCost;

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Trader.class).withName(new Random().nextInt() + ""));

        var trader = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Trader");

        trader.spawn(creator.getLocation());
        Traits.put(trader.getId(), trader.getTrait(Trader.class));
        trader.addTrait(Trader.class);
    }

    public void CreateNPCTrader(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NPCTrader.class).withName(new Random().nextInt() + ""));

        var npcTrader = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "NPCTrader");

        npcTrader.addTrait(NPCTrader.class);
        Traits.put(npcTrader.getId(), npcTrader.getTrait(NPCTrader.class));
        npcTrader.spawn(creator.getLocation());
    }

    public void CreateLicensor(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Licensor.class).withName(new Random().nextInt() + ""));

        var loaner = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Licensor");

        loaner.addTrait(Licensor.class);
        Traits.put(loaner.getId(), loaner.getTrait(Licensor.class));
        loaner.spawn(creator.getLocation());
    }

    public net.citizensnpcs.api.npc.NPC GetNPC(int id) {
        for (net.citizensnpcs.api.npc.NPC npc:
                CitizensAPI.getNPCRegistry()) {
            if (Traits.get(npc.getId()) != null) {
                if (npc.getId() == id) {
                    return npc;
                }
            }
        }

        return null;
    }

    public void UpdateNPC(Trait trait) { // Перед сохранением
        for (net.citizensnpcs.api.npc.NPC npc:
             CitizensAPI.getNPCRegistry()) {
            if (Traits.get(npc.getId()) != null) {
                Traits.put(npc.getId(), npc.getTrait(trait.getClass()));
            }
        }
    }

    public void DeleteNPC(int id) {
        var npc = GetNPC(id);
        if (npc != null) {
            Traits.remove(npc.getId());
            npc.destroy();
        }
    }

    private void DeleteAllNPC() {
        for (net.citizensnpcs.api.npc.NPC npc:
                CitizensAPI.getNPCRegistry()) {
            if (Traits.get(npc.getId()) != null) {
                Traits.remove(npc.getId());
                npc.destroy();
            }
        }
    }

    public void SaveNPC(String fileName) throws IOException {
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
