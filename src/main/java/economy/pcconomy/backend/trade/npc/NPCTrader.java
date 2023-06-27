package economy.pcconomy.backend.trade.npc;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.town.Town;
import economy.pcconomy.frontend.ui.windows.npcTrade.NPCTraderWindow;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

import java.util.Objects;

@TraitName("NPCTrader")
public class NPCTrader extends Trait {
    public NPCTrader() {
        super("NPCTrader");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;
        PcConomy.GlobalTownWorker.getTownObject(TownyAPI.getInstance()
                .getTownName(this.getNPC().getStoredLocation())).generateLocalPrices();
        player.openInventory(Objects.requireNonNull(NPCTraderWindow.GetWindow(player, this.getNPC())));
    }

    @EventHandler
    public void onClick(NPCLeftClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;
        PcConomy.GlobalTownWorker.getTownObject(TownyAPI.getInstance()
                .getTownName(this.getNPC().getStoredLocation())).generateLocalPrices();
        Town.sellResourceToStorage(PcConomy.GlobalTownWorker.getTownObject(TownyAPI.getInstance()
                .getTownName(this.getNPC().getStoredLocation())), player.getInventory().getItemInMainHand(), player);
    }
}
