package economy.pcconomy.backend.npc.traits;

import economy.pcconomy.frontend.ui.windows.Window;
import economy.pcconomy.frontend.ui.windows.license.LicensorWindow;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

@TraitName("Licensor")
public class Licensor extends Trait {
    public Licensor() {
        super("Licensor");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        Window.openWindow(event.getClicker(), new LicensorWindow());
    }
}
