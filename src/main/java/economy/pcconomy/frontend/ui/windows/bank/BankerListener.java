package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.ItemManager;
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
            var amount = Double.parseDouble(ItemManager.getName(option).
                    replace(CashManager.currencySigh, ""));

            if (amount > 0) PcConomy.GlobalBank.giveCashToPlayer(amount, player);
            else PcConomy.GlobalBank.takeCashFromPlayer(Math.abs(amount), player);

            event.setCancelled(true);
        }
    }
}
