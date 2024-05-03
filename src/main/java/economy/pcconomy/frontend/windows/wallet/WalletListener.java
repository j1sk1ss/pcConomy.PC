package economy.pcconomy.frontend.windows.wallet;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.cash.Wallet;
import economy.pcconomy.frontend.windows.WindowListener;
import economy.pcconomy.frontend.windows.Window;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.j1sk1ss.itemmanager.manager.Manager;

import lombok.experimental.ExtensionMethod;


@ExtensionMethod({Manager.class, CashManager.class})
public class WalletListener extends WindowListener implements Listener  {
    @EventHandler
    public void onWalletUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_AIR) return;

        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        var wallet = Wallet.isWallet(item) ? new Wallet(item) : null;
        if (wallet != null) {
            switch (event.getAction()) {
                case LEFT_CLICK_AIR -> player.openInventory(WalletWindow.putWindow(player, wallet));
                case RIGHT_CLICK_AIR -> player.openInventory(WalletWindow.withdrawWindow(player, wallet));
                default -> throw new IllegalArgumentException("Unexpected value: " + event.getAction());
            }

            event.setCancelled(true);
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var currentItem = player.getInventory().getItemInMainHand();
        if (currentItem.getAmount() > 1 && Wallet.isWallet(currentItem)) {
            player.sendMessage("Выберите один кошелёк");
            event.setCancelled(true);
            return;
        }

        var wallet = Wallet.isWallet(currentItem) ? new Wallet(player.getInventory().getItemInMainHand()) : null;
        if (wallet == null) return;

        player.getInventory().setItemInMainHand(null);
        if (Window.isThisWindow(event, player, "Кошелёк")) {
            var option = event.getCurrentItem();
            if (option == null) return;

            if (option.getLoreLines().size() < 2) return;
            var amount = option.getDoubleFromContainer("item-wallet-value");

            if (amount > 0) {
                player.giveCashToPlayer(Math.abs(amount), true);
                wallet.changeCashInWallet(-amount);
            }
            else {
                player.takeCashFromPlayer(Math.abs(amount), true);
                wallet.changeCashInWallet(Math.abs(amount));
            }

            player.closeInventory();
            wallet.giveWallet(player);
            event.setCancelled(true);
        }
    }
}