package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.cash.scripts.ChangeWorker;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BankerWindow {
    public static Inventory GetWindow(Player player) {
        // Создание окна банкира
        var window = Bukkit.createInventory(player, 27, Component.text("Банк"));

        var enableBalance = PcConomy.GlobalBank.GetUsefulAmountOfBudget();
        var playerBalance = new BalanceWorker().getBalance(player);
        var cashInInventory = new Cash().AmountOfCashInInventory(player);

        // Кнопки вывода
        for (var i = 0; i < 9; i++) {
            if (i == 0 && playerBalance < enableBalance) {
                window.setItem(0, ItemWorker.SetName(new ItemStack(Material.GREEN_WOOL),
                        Math.round(playerBalance * 100) / 100 + CashWorker.currencySigh));
                window.setItem(18, ItemWorker.SetName(new ItemStack(Material.RED_WOOL),
                        "-" + cashInInventory + CashWorker.currencySigh));
            }

            if (enableBalance >= ChangeWorker.Denomination.get(i) && playerBalance >= ChangeWorker.Denomination.get(i))
                for (var j = i; j < 8; j++)
                    window.setItem(j + 1, ItemWorker.SetName(new ItemStack(Material.GREEN_WOOL),
                            ChangeWorker.Denomination.get(j) + CashWorker.currencySigh));

            if (cashInInventory >= ChangeWorker.Denomination.get(i))
                for (var j = i; j < 8; j++)
                    window.setItem(j + 19, ItemWorker.SetName(new ItemStack(Material.RED_WOOL),
                            "-" + ChangeWorker.Denomination.get(j) + CashWorker.currencySigh));
        }

        return window;
    }
}
