package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.BalanceManager;

import economy.pcconomy.frontend.ui.windows.Window;
import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;


@ExtensionMethod({Manager.class})
public class BankerWindow extends Window {
    public Inventory generateWindow(Player player) {
        var window = Bukkit.createInventory(player, 54, Component.text("Мир-Банк"));

        var enableBalance   = PcConomy.GlobalBank.DayWithdrawBudget;
        var playerBalance   = BalanceManager.getBalance(player);
        var cashInInventory = CashManager.amountOfCashInInventory(player, false);
        var textBalance = playerBalance + "";
        var charArray   = textBalance.toCharArray();
        
        for (var i = 9; i < Math.min(charArray.length + 9, 27); i++) {
            var currentChar = charArray[i - 9];
            switch (currentChar) {
                case 'E' -> {
                    window.setItem(i, new Item("Баланс", textBalance, Material.PAPER, 1, 17000));
                    continue;
                } //TODO: DATA MODEL
                case '.' -> {
                    window.setItem(i, new Item("Баланс", textBalance, Material.PAPER, 1, 17001));
                    continue;
                }
            }

            window.setItem(i, new Item("Баланс", textBalance, Material.PAPER, 1, 17000 + Integer.parseInt(currentChar + ""))); //TODO: DATA MODEL
        }

        printButtons(playerBalance, window, enableBalance, cashInInventory);
        return window;
    }

    public static Inventory regenerateWindow(Player player, Inventory inventory) {
        inventory.clear();

        var enableBalance   = PcConomy.GlobalBank.DayWithdrawBudget;
        var playerBalance   = BalanceManager.getBalance(player);
        var cashInInventory = CashManager.amountOfCashInInventory(player, false);
        var textBalance = playerBalance + "";
        var charArray   = textBalance.toCharArray();

        for (var i = 9; i < Math.min(charArray.length + 9, 27); i++) {
            var currentChar = charArray[i - 9];
            switch (currentChar) {
                case 'E' -> {
                    inventory.setItem(i, new Item("Баланс", textBalance, Material.PAPER, 1, 17000));
                    continue;
                } //TODO: DATA MODEL
                case '.' -> {
                    inventory.setItem(i, new Item("Баланс", textBalance, Material.PAPER, 1, 17001));
                    continue;
                }
            }

            inventory.setItem(i, new Item("Баланс", textBalance, Material.PAPER, 1, 17000 + Integer.parseInt(currentChar + ""))); //TODO: DATA MODEL
        }

        printButtons(playerBalance, inventory, enableBalance, cashInInventory);
        return inventory;
    }

    private static void printButtons(double playerBalance, Inventory inventory, double enableBalance, double cashInInventory) {
        for (var i = 0; i < 8; i++) {
            if (i == 0 && playerBalance < enableBalance) {// TODO: DATA MODEL
                var button = new Item("Снять максимум", "\n" + Math.round(playerBalance * 100) / 100 + CashManager.currencySigh, Material.PAPER, 1, 17000);
                button.setDouble2Container(Math.round(playerBalance * 100) / 100, "item-bank-value");
                inventory.setItem(41, button);
            }

            if (enableBalance >= CashManager.Denomination.get(i) && playerBalance >= CashManager.Denomination.get(i)) printButtons("\n", 41, i, inventory);

            if (i == 0) { // TODO: DATA MODEL
                var button = new Item("Положить все средства", "\n-" + cashInInventory + CashManager.currencySigh, Material.PAPER, 1, 17000);
                button.setDouble2Container(Double.parseDouble("\n-" + cashInInventory), "item-bank-value");
                inventory.setItem(36, button);
            }
                        
            if (cashInInventory >= CashManager.Denomination.get(i)) printButtons("\n-", 36, i, inventory);
        }
    }

    private static void printButtons(String thing, int position, int enabled, Inventory window) { // TODO: DATA MODEL
        for (var j = enabled; j < 8; j++) {
            var button = new Item("Действие", thing + CashManager.Denomination.get(j) + CashManager.currencySigh, Material.PAPER, 1, 17000);
            button.setDouble2Container(Double.parseDouble(thing + CashManager.Denomination.get(j)), "item-bank-value");
            window.setItem(j + (position + 5 * (j / 4)), button);
        }
    }
}
