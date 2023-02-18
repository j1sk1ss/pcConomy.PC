package economy.pcconomy.frontend.ui.windows.mayor;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MayorListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player player1)
                if (player1.equals(player)) {
                    if (event.getView().getTitle().contains("Меню")) {
                        if (!player.getInventory().contains(event.getCurrentItem())) {
                            PcConomy.GlobalNPC.BuyTrader(player);
                        }

                        event.setCancelled(true);
                    }
                }
    }

}
