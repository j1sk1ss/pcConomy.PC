package economy.pcconomy.backend.trade.npc;

import economy.pcconomy.frontend.ui.windows.NPCTraderWindow;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

@TraitName("NPCTrader")
public class NPCTrader extends Trait {
    public NPCTrader() {
        super("NPCTrader");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;
        player.openInventory(NPCTraderWindow.GetNPCTraderWindow(player, this.getNPC()));
    }
}
