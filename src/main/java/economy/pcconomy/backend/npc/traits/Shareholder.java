package economy.pcconomy.backend.npc.traits;

import economy.pcconomy.frontend.windows.shareholder.ShareholderWindow;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

import org.bukkit.event.EventHandler;


@TraitName("Shareholder")
public class Shareholder extends Trait {
    public Shareholder() {
        super("Shareholder");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        ShareholderWindow.generateWindow(event.getClicker());
    }
}
