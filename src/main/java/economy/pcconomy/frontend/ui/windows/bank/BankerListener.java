package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BankerListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() != null) {
            if (event.getInventory().getHolder() instanceof Player player1)
                if (event.getView().getTitle().equals("Банк") && player1.equals(player)) {

                    var amount = Double.parseDouble(ItemWorker.GetName(event.getCurrentItem()).
                            replace(CashWorker.currencySigh, ""));

                    if (amount > 0) PcConomy.GlobalBank.PlayerWithdrawCash(amount, player);
                    else PcConomy.GlobalBank.PlayerPutCash(Math.abs(amount), player);

                    event.setCancelled(true);
                }
        }
    }
}
