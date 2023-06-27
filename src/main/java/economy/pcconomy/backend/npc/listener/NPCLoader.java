package economy.pcconomy.backend.npc.listener;

import economy.pcconomy.backend.bank.npc.Banker;
import economy.pcconomy.backend.bank.npc.Loaner;
import economy.pcconomy.backend.bank.npc.NPCLoaner;
import economy.pcconomy.backend.license.npc.Licensor;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.trade.npc.NPCTrader;
import economy.pcconomy.backend.trade.npc.Trader;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.trait.TraitInfo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static economy.pcconomy.PcConomy.GlobalNPC;

public class NPCLoader implements Listener {
    @EventHandler
    public void loadNPC(CitizensEnableEvent event) {
        if (GlobalNPC == null) GlobalNPC = new NPC();

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Trader.class).withName("Trader"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NPCLoaner.class).withName("npcloaner"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Loaner.class).withName("loaner"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NPCTrader.class).withName("npctrader"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Banker.class).withName("banker"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Licensor.class).withName("licensor"));

        GlobalNPC.updateNPC();
    }
}
