package economy.pcconomy.bank.npc;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import economy.pcconomy.bank.ui.Window;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

@TraitName("Banker")
public class Banker extends Trait {
    public Banker() {
        super("Banker");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) throws NotRegisteredException {
        var player = event.getClicker();
        //var currentTown = TownyAPI.getInstance().getTownBlock(player.getLocation()).getTown();
        //if (currentTown == null) return;

        //var currentTownName = currentTown.getName();
        //if (!TownyAPI.getInstance().getTownName(this.getNPC().getStoredLocation()).equals(currentTownName)) return;

        // Открыть ЮАЙ
        Window.OpenBankerWindow(player);
    }
}
