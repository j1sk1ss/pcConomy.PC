package economy.pcconomy.backend.npc;

import economy.pcconomy.backend.bank.npc.Banker;
import economy.pcconomy.backend.bank.npc.Loaner;
import economy.pcconomy.backend.trade.npc.Trader;
import economy.pcconomy.backend.trade.scripts.TraderWorker;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Random;

public class NPC {
    public static void CreateBanker(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Banker.class).withName("Banker" + new Random().nextInt()));
        var banker = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Banker");
        banker.addTrait(Banker.class);
        banker.spawn(creator.getLocation());
    }

    public static void CreateLoaner(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Loaner.class).withName("Loaner" + new Random().nextInt()));
        var loaner = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Loaner");
        loaner.addTrait(Loaner.class);
        loaner.spawn(creator.getLocation());
    }

    public static void CreateTrader(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Trader.class).withName("Trader" + new Random().nextInt()));

        var name = "Trader" + new Random().nextInt();
        var trader = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        TraderWorker.CreateTrader(creator, 1000);

        trader.addTrait(TraderWorker.GetTrader(name));
        trader.spawn(creator.getLocation());
    }
}
