package economy.pcconomy.frontend.windows.wallet;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.cash.Wallet;
import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;


@ExtensionMethod({Manager.class, CashManager.class})
public class WalletWindow {
    public static Inventory putWindow(Player player, Wallet wallet) {
        var window = Bukkit.createInventory(player, 9, Component.text("Кошелёк"));
        var cashInInventory = Math.min(player.amountOfCashInInventory(true), wallet.Capacity - wallet.Amount);

        for (var i = 0; i < 8; i++) { // TODO: DATA MODEL
            if (i == 0) {
                var button = new Item("Положить все средства", "\n-" + cashInInventory + CashManager.currencySigh, Material.PAPER, 1, 17000);
                button.setDouble2Container(Double.parseDouble("\n-" + cashInInventory), "item-wallet-value");
                window.setItem(0, button);
            }

            if (cashInInventory >= CashManager.Denomination.get(i))
                printButtons("\n-", window);
        }

        return window;
    }

    public static Inventory withdrawWindow(Player player, Wallet wallet) {
        var window = Bukkit.createInventory(player, 9, Component.text("Кошелёк"));
        var cashInWallet = wallet.Amount;

        for (var i = 0; i < 8; i++) { // TODO: DATA MODEL
            if (i == 0) {
                var button = new Item("Снять максимум", "\n" + Math.round(cashInWallet * 100) / 100 + CashManager.currencySigh, Material.PAPER, 1, 17000);
                button.setDouble2Container(Math.round(cashInWallet * 100) / 100, "item-wallet-value");
                window.setItem(0, button);
            }

            if (cashInWallet >= CashManager.Denomination.get(i))
                printButtons("\n", window);
        }

        return window;
    }

    private static void printButtons(String thing, Inventory window) {
        for (var j = 1; j < 8; j++) { // TODO: DATA MODEL
            var button = new Item("Действия", thing + CashManager.Denomination.get(j) + CashManager.currencySigh, Material.PAPER, 1, 17000);
            button.setDouble2Container(Double.parseDouble(thing + CashManager.Denomination.get(j)), "item-wallet-value");
            window.setItem(j, button);
        }
    }
}
