package economy.pcconomy.backend.npc.traits;

import org.bukkit.event.EventHandler;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import economy.pcconomy.frontend.BankerWindow;
import net.citizensnpcs.api.event.NPCRightClickEvent;


@TraitName("Banker")
public class Banker extends Trait {
    public Banker() {
        super("Banker");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        BankerWindow.generateWindow(event.getClicker());
    }
}
