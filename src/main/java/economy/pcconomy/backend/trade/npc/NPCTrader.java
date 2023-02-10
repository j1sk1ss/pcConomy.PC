package economy.pcconomy.backend.trade.npc;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.backend.town.Town;
import economy.pcconomy.backend.town.scripts.TownWorker;
import economy.pcconomy.frontend.ui.windows.NPCTraderWindow;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

@TraitName("NPCTrader")
public class NPCTrader extends Trait {
    public NPCTrader() {
        super("NPCTrader");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;
        TownWorker.GetTownObject(TownyAPI.getInstance()
                .getTownName(this.getNPC().getStoredLocation())).GenerateLocalPrices();
        player.openInventory(NPCTraderWindow.GetNPCTraderWindow(player, this.getNPC()));
    }

    @EventHandler
    public void onClick(NPCLeftClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;
        TownWorker.GetTownObject(TownyAPI.getInstance()
                .getTownName(this.getNPC().getStoredLocation())).GenerateLocalPrices();
        Town.SellResourceToStorage(TownWorker.GetTownObject(TownyAPI.getInstance()
                .getTownName(this.getNPC().getStoredLocation())), player.getInventory().getItemInMainHand(), player);
    }
}
