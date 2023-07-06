package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
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
            switch (event.getView().getTitle()) {
                case "Банк" -> {
                    switch (BankerWindow.Panel.click(event.getSlot()).getName()) {
                        case "Внести деньги" -> player.openInventory(BankerWindow.putWindow(player));
                        case "Вывести деньги" -> player.openInventory(BankerWindow.withdrawWindow(player));
                    }
                }
                case "Банк-Снятие" -> {
                    var amount = Double.parseDouble(ItemManager.getName(option).
                            replace(CashManager.currencySigh, ""));
                    PcConomy.GlobalBank.giveCashToPlayer(amount, player);
                }
                case "Банк-Внесение" -> {
                    var amount = Double.parseDouble(ItemManager.getName(option).
                            replace(CashManager.currencySigh, ""));
                    PcConomy.GlobalBank.takeCashFromPlayer(Math.abs(amount), player);
                }
            }

            event.setCancelled(true);
        }
    }
}
