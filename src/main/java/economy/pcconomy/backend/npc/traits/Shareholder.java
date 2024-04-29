package economy.pcconomy.backend.npc.traits;

import economy.pcconomy.frontend.windows.Window;
import economy.pcconomy.frontend.windows.shareholder.ShareholderWindow;

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
        System.out.print("Click");
        if (!event.getNPC().equals(this.getNPC())) return;
        System.out.print("Click (1)");
        Window.openWindow(event.getClicker(), new ShareholderWindow());
        System.out.print("Click (2)");
    }
}
