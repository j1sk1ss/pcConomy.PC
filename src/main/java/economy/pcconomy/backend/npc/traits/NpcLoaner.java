package economy.pcconomy.backend.npc.traits;

import org.bukkit.event.EventHandler;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import economy.pcconomy.frontend.NPCLoanWindow;
import net.citizensnpcs.api.event.NPCRightClickEvent;


@TraitName("NPCLoaner")
public class NpcLoaner extends Trait {
    public NpcLoaner() {
        super("NPCLoaner");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        NPCLoanWindow.generateWindow(event.getClicker());
    }
}
