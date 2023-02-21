package economy.pcconomy.frontend.ui.windows.mayor;

import economy.pcconomy.PcConomy;

import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.frontend.ui.Window;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MayorListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (Window.isThisWindow(event, player, "Меню")) {
            var option = ItemWorker.GetName(event.getCurrentItem());

            if (option.equals("Установить торговца")) PcConomy.GlobalNPC.BuyTrader(player);
            if (option.equals("Установить кредитора")) PcConomy.GlobalNPC.BuyLoaner(player);
            event.setCancelled(true);
        }
    }
}
