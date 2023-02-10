package economy.pcconomy.backend.npc;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.npc.Banker;
import economy.pcconomy.backend.bank.npc.Loaner;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.license.npc.Licensor;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.license.scripts.LicenseWorker;
import economy.pcconomy.backend.trade.npc.NPCTrader;
import economy.pcconomy.backend.trade.npc.Trader;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;

public class NPC {

    public static Dictionary<net.citizensnpcs.api.npc.NPC, Trait> Traits = new Hashtable<>(); // Для сохранения

    public static void CreateBanker(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Banker.class).withName("Banker" + new Random().nextInt()));
        var banker = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Banker");

        banker.addTrait(Banker.class);
        Traits.put(banker, banker.getTrait(Banker.class));
        banker.spawn(creator.getLocation());
    }

    public static void CreateLoaner(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Loaner.class).withName("Loaner" + new Random().nextInt()));
        var loaner = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Loaner");

        loaner.addTrait(Loaner.class);
        Traits.put(loaner, loaner.getTrait(Loaner.class));
        loaner.spawn(creator.getLocation());
    }

    private final static double traderCost = 3000d;

    public static void CreateTrader(Player creator) {
        var cash = new Cash();
        if (cash.AmountOfCashInInventory(creator) < traderCost) return;

        if (LicenseWorker.GetLicense(creator, LicenseType.Market) == null) return;
        if (LicenseWorker.isOverdue(LicenseWorker.GetLicense(creator, LicenseType.Market))) return;

        cash.TakeCashFromInventory(traderCost, creator);
        PcConomy.GlobalBank.BankBudget += traderCost;

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Trader.class).withName("Trader" + new Random().nextInt()));
        var trader = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Trader");

        trader.spawn(creator.getLocation());
        Traits.put(trader, trader.getTrait(Trader.class));
        trader.addTrait(Trader.class);
    }

    public static void CreateNPCTrader(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Loaner.class).withName("NPCTrader" + new Random().nextInt()));
        var npcTrader = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "NPCTrader");

        npcTrader.addTrait(NPCTrader.class);
        Traits.put(npcTrader, npcTrader.getTrait(NPCTrader.class));
        npcTrader.spawn(creator.getLocation());
    }

    public static void CreateLicensor(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Licensor.class).withName("Licensor" + new Random().nextInt()));
        var loaner = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Licensor");

        loaner.addTrait(Licensor.class);
        Traits.put(loaner, loaner.getTrait(Licensor.class));
        loaner.spawn(creator.getLocation());
    }

    public static net.citizensnpcs.api.npc.NPC GetNPC(int id) {
        for (net.citizensnpcs.api.npc.NPC npc:
                CitizensAPI.getNPCRegistry()) {
            if (Traits.get(npc) != null) {
                if (npc.getId() == id) {
                    return npc;
                }
            }
        }

        return null;
    }

    public static void UpdateNPC(Trait trait) { // Перед сохранением
        for (net.citizensnpcs.api.npc.NPC npc:
             CitizensAPI.getNPCRegistry()) {
            if (Traits.get(npc) != null) {
                Traits.put(npc, npc.getTrait(trait.getClass()));
            }
        }
    }

    public static void DeleteNPC(int id) {
        var npc = GetNPC(id);
        if (npc != null) {
            Traits.remove(npc);
            npc.destroy();
        }
    }

    private static void DeleteAllNPC() {
        for (net.citizensnpcs.api.npc.NPC npc:
                CitizensAPI.getNPCRegistry()) {
            if (Traits.get(npc) != null) {
                Traits.remove(npc);
                npc.destroy();
            }
        }
    }
}
