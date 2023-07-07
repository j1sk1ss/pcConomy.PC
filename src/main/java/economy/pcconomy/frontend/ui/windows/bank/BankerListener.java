package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.frontend.ui.windows.Window;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class BankerListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (Window.isThisWindow(event, player, "Банк")) {
            var option = Objects.requireNonNull(event.getCurrentItem());
            var amount = ItemManager.getPriceFromLore(option, 0);

            if (amount > 0)
                PcConomy.GlobalBank.giveCashToPlayer(amount, player);
            else
                PcConomy.GlobalBank.takeCashFromPlayer(Math.abs(amount), player);

            event.setCancelled(true);
        }
    }
}
