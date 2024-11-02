package economy.pcconomy.backend.npc.traits;

import org.bukkit.event.EventHandler;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import economy.pcconomy.frontend.LicensorWindow;
import net.citizensnpcs.api.event.NPCRightClickEvent;


@TraitName("Licensor")
public class Licensor extends Trait {
    public Licensor() {
        super("Licensor");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        LicensorWindow.generateWindow(event.getClicker());
    }
}
