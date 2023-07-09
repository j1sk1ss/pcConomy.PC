package economy.pcconomy.backend.npc.traits;

import economy.pcconomy.frontend.ui.windows.Window;
import economy.pcconomy.frontend.ui.windows.shareholder.ShareholderWindow;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

import org.bukkit.event.EventHandler;

@TraitName("Trader")
public class Shareholder extends Trait {
    public Shareholder() {
        super("Shareholder");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;
        Window.OpenWindow(player, new ShareholderWindow());
    }
}
