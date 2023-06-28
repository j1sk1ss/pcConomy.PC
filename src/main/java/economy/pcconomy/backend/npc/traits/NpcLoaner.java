package economy.pcconomy.backend.npc.traits;

import economy.pcconomy.frontend.ui.Window;
import economy.pcconomy.frontend.ui.windows.loans.npcLoan.NPCLoanWindow;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

@TraitName("NPCLoaner")
public class NpcLoaner extends Trait {
    public NpcLoaner() {
        super("NPCLoaner");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        var player = event.getClicker();

        Window.OpenWindow(player, new NPCLoanWindow());
    }
}
