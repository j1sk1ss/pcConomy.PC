package economy.pcconomy.backend.license.npc;

import economy.pcconomy.frontend.ui.Window;
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
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;
        Window.OpenBankerWindow(player);
    }
}
