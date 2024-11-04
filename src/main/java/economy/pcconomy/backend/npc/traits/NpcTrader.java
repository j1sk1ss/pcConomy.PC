package economy.pcconomy.backend.npc.traits;

import org.bukkit.event.EventHandler;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import com.palmergames.bukkit.towny.TownyAPI;
import net.potolotcraft.gorodki.GorodkiUniverse;
import economy.pcconomy.frontend.NPCTraderWindow;
import net.citizensnpcs.api.event.NPCRightClickEvent;


@TraitName("NPCTrader")
public class NpcTrader extends Trait {
    public NpcTrader() {
        super("NPCTrader");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        var player = event.getClicker();
        if (!event.getNPC().equals(this.getNPC())) return;

        try {
            GorodkiUniverse.getInstance().getNPCGorod(TownyAPI.getInstance().getTownUUID(this.getNPC().getStoredLocation())).generateLocalPrices();
            NPCTraderWindow.generateWindow(player, this.getNPC());
        }
        catch (Exception e) {
            player.sendMessage("Я вообще ничего не понимаю! Где я?");
        }
    }
}
