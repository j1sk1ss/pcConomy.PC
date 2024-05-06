package economy.pcconomy.frontend.wallet;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.cash.Wallet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.j1sk1ss.itemmanager.manager.Manager;

import lombok.experimental.ExtensionMethod;


@ExtensionMethod({Manager.class, CashManager.class})
public class WalletListener implements Listener  {
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
}