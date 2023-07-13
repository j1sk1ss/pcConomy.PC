package economy.pcconomy.frontend.ui.windows.wallet;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.cash.items.Wallet;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.frontend.ui.windows.Window;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class WalletListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var wallet = player.getInventory().getItemInMainHand();

        if (Window.isThisWindow(event, player, "Кошелёк")) {
            var option = Objects.requireNonNull(event.getCurrentItem());
            var amount = ItemManager.getPriceFromLore(option, 0);

            if (amount > 0) {
                Wallet.changeCashInWallet(wallet, player, -amount);
                CashManager.giveCashToPlayer(amount, player, true);
            }
            else {
                CashManager.takeCashFromPlayer(amount, player, true);
                ItemManager.takeItems(wallet, player);

                Wallet.changeCashInWallet(wallet, player, amount);
            }

            player.closeInventory();
            event.setCancelled(true);
        }
    }
}