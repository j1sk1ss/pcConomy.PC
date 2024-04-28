package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.BalanceManager;

import economy.pcconomy.frontend.ui.windows.Window;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.j1sk1ss.itemmanager.manager.Item;


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
            if (i == 0 && playerBalance < enableBalance)
                inventory.setItem(41, new Item("Снять максимум", //TODO: DATA MODEL
                        "\n" + Math.round(playerBalance * 100) / 100 + CashManager.currencySigh, Material.PAPER, 1, 17000));

            if (enableBalance >= CashManager.Denomination.get(i) && playerBalance >= CashManager.Denomination.get(i)) printButtons("\n", 41, i, inventory);

            if (i == 0)
                inventory.setItem(36, new Item("Положить все средства", //TODO: DATA MODEL
                        "\n-" + cashInInventory + CashManager.currencySigh, Material.PAPER, 1, 17000));

            if (cashInInventory >= CashManager.Denomination.get(i)) printButtons("\n-", 36, i, inventory);
        }
    }

    private static void printButtons(String thing, int position, int enabled, Inventory window) {
        for (var j = enabled; j < 8; j++) //TODO: DATA MODEL
            window.setItem(j + (position + 5 * (j / 4)), new Item("Действие",
                thing + CashManager.Denomination.get(j) + CashManager.currencySigh, Material.PAPER, 1, 17000));
    }
}
