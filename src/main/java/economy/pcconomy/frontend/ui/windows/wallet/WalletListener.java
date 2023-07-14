package economy.pcconomy.frontend.ui.windows.wallet;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.cash.items.Wallet;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.frontend.ui.windows.Window;

import org.bukkit.block.data.type.Wall;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;

public class WalletListener implements Listener {
    @EventHandler
    public void onWalletUse(PlayerInteractEvent event){
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_AIR) return;

        var player = event.getPlayer();
        var wallet = Wallet.isWallet(player.getInventory().getItemInMainHand()) ?
                new Wallet(player.getInventory().getItemInMainHand()) :
                null;

        if (wallet != null) {
            switch (event.getAction()) {
                case LEFT_CLICK_AIR ->
                        player.openInventory(WalletWindow.putWindow(player, wallet));
                case RIGHT_CLICK_AIR ->
                        player.openInventory(WalletWindow.withdrawWindow(player, wallet));
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var wallet = new Wallet(player.getInventory().getItemInMainHand());

        if (Window.isThisWindow(event, player, "Кошелёк")) {
            var amount = ItemManager.getPriceFromLore(Objects.requireNonNull(event.getCurrentItem()), 0);

            wallet.Amount -= amount;
            if (amount > 0) CashManager.giveCashToPlayer(amount, player, true);
            else CashManager.takeCashFromPlayer(Math.abs(amount), player, true);

            player.closeInventory();
            event.setCancelled(true);
        }
    }
}