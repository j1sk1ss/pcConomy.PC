package economy.pcconomy.frontend.ui.listener;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.backend.town.Town;
import economy.pcconomy.backend.town.scripts.TownWorker;

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

                    if (event.getView().getTitle().contains("Магазин")) {
                        var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);
                        if (!player.getInventory().contains(event.getCurrentItem())) {
                            if (event.isLeftClick()) {
                                Town.BuyResourceFromStorage(TownWorker.GetTownObject(town.getName()),
                                        event.getCurrentItem(), player);
                            }
                        }

                        event.setCancelled(true);
                    }
                }
    }
}


