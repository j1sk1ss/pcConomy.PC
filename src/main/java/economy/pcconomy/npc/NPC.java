package economy.pcconomy.npc;

import economy.pcconomy.bank.npc.Banker;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class NPC {
    public static void CreateBanker(Player creator) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Banker.class).withName("Banker"));
        var banker = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Banker");
        banker.addTrait(Banker.class);
        banker.spawn(creator.getLocation());
    }
}
