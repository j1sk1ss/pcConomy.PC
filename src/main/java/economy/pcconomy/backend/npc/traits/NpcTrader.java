package economy.pcconomy.backend.npc.traits;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.town.objects.town.NpcTown;
import economy.pcconomy.frontend.ui.windows.npcTrade.NPCTraderWindow;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

import java.util.Objects;

@TraitName("NPCTrader")
public class NpcTrader extends Trait {
    public NpcTrader() {
        super("NPCTrader");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;
        ((NpcTown)PcConomy.GlobalTownManager.getTown(TownyAPI.getInstance().getTownName(
                this.getNPC().getStoredLocation()))).generateLocalPrices();
        player.openInventory(Objects.requireNonNull(NPCTraderWindow.generateWindow(player, this.getNPC())));
    }

    @EventHandler
    public void onClick(NPCLeftClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;
        ((NpcTown)PcConomy.GlobalTownManager.getTown(TownyAPI.getInstance().getTownName(
                this.getNPC().getStoredLocation()))).generateLocalPrices();
        ((NpcTown)PcConomy.GlobalTownManager.getTown(TownyAPI.getInstance().getTownName(
                this.getNPC().getStoredLocation()))).sellResourceToStorage(player.getInventory().getItemInMainHand(), player);
    }
}
