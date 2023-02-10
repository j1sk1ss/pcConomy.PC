package economy.pcconomy.frontend.ui.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.Town;
import economy.pcconomy.backend.town.scripts.TownWorker;
import economy.pcconomy.frontend.ui.windows.NPCTraderWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class NPCTraderListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player player1)
                if (player1.equals(player)) {

                    var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);
                    var title = event.getView().getTitle();

                    if (title.contains("Магазин")) {
                        if (!player.getInventory().contains(event.getCurrentItem())) {
                            if (event.isLeftClick()) {
                                Town.BuyResourceFromStorage(TownWorker.GetTownObject(town.getName()),
                                        event.getInventory().getItem(4), player);
                            } else {
                                Town.SellResourceToStorage(TownWorker.GetTownObject(town.getName()),
                                        event.getInventory().getItem(4), player);
                            }
                        }
                    }

                    event.setCancelled(true);
                }
    }
}


