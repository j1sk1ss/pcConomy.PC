package economy.pcconomy.npc;

import economy.pcconomy.bank.npc.Banker;
import economy.pcconomy.bank.npc.Loaner;
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
}
