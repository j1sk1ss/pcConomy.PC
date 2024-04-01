package economy.pcconomy.backend.npc.loader;

import economy.pcconomy.backend.npc.traits.Banker;
import economy.pcconomy.backend.npc.traits.Loaner;
import economy.pcconomy.backend.npc.traits.NpcLoaner;
import economy.pcconomy.backend.npc.traits.Licensor;
import economy.pcconomy.backend.npc.traits.NpcTrader;
import economy.pcconomy.backend.npc.traits.Trader;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.trait.TraitInfo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static economy.pcconomy.PcConomy.GlobalNPC;


public class NpcLoader implements Listener {
    /**
     * When Citizens plugin enables - this plugin register all traits
     * @param event Citizens enable event
     */
    @EventHandler
    public void loadNPC(CitizensEnableEvent event) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Trader.class).withName("Trader"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NpcLoaner.class).withName("npcloaner"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Loaner.class).withName("loaner"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NpcTrader.class).withName("npctrader"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Banker.class).withName("banker"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Licensor.class).withName("licensor"));

        GlobalNPC.reloadNPC();
    }
}
