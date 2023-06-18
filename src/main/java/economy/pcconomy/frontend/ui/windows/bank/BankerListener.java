package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.scripts.CashManager;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.frontend.ui.Window;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BankerListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var option = event.getCurrentItem();

        if (Window.isThisWindow(event, player, "Банк") && option != null) {
            var amount = Double.parseDouble(ItemWorker.GetName(option).
                    replace(CashManager.currencySigh, ""));

            if (amount > 0) PcConomy.GlobalBank.GiveCashToPlayer(amount, player);
            else PcConomy.GlobalBank.TakeCashFromPlayer(Math.abs(amount), player);

            event.setCancelled(true);
        }
    }
}
