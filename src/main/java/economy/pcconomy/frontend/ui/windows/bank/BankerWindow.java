package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.BalanceManager;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.frontend.ui.windows.IWindow;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BankerWindow implements IWindow {
    public Inventory generateWindow(Player player) {
        var window = Bukkit.createInventory(player, 54, Component.text("Банк"));

        var enableBalance   = PcConomy.GlobalBank.getUsefulAmountOfBudget();
        var playerBalance   = new BalanceManager().getBalance(player);
        var cashInInventory = new CashManager().amountOfCashInInventory(player);

        var textBalance = playerBalance + "";
        var charArray  = textBalance.toCharArray();
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

            window.setItem(i, new Item("Баланс", textBalance, Material.PAPER, 1,
                    17000 + Integer.parseInt(currentChar+""))); //TODO: DATA MODEL
        }

        for (var i = 0; i < 8; i++) {
            if (i == 0 && playerBalance < enableBalance)
                window.setItem(41, new Item("Снять максимум", //TODO: DATA MODEL
                        Math.round(playerBalance * 100) / 100 + CashManager.currencySigh, Material.PAPER, 1, 17000));

            if (enableBalance >= CashManager.Denomination.get(i) && playerBalance >= CashManager.Denomination.get(i))
                printButtons(41, window);

            if (i == 0)
                window.setItem(36, new Item("Положить все средства", //TODO: DATA MODEL
                        "-" + cashInInventory + CashManager.currencySigh, Material.PAPER, 1, 17000));

            if (cashInInventory >= CashManager.Denomination.get(i))
                printButtons(36, window);
        }

        return window;
    }

    private void printButtons(int position, Inventory window) {
        for (var j = 1; j < 8; j++) //TODO: DATA MODEL
            window.setItem(j + (position + 5 * (j / 4)), new Item("Действия",
                    (position == 36 ? "-" : "") + CashManager.Denomination.get(j) + CashManager.currencySigh, Material.PAPER, 1, 17000));
    }
}
