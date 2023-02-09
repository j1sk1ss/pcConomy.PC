package economy.pcconomy.frontend.ui.windows;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.scripts.ChangeWorker;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BankerWindow {
    public static Inventory GetBankerWindow(Player player) {
        // Создание окна банкира
        var window = Bukkit.createInventory(player, 27, "Банк");

        var size = PcConomy.GlobalBank.GetUsefulAmountOfBudget();
        var balance = new BalanceWorker().getBalance(player);
        // Кнопки вывода
        for (var i = 0; i < 9; i++) {
            if (size >= ChangeWorker.Denomination.get(i) && balance >= ChangeWorker.Denomination.get(i)) {
                for (var j = i; j < 9; j++) {
                    window.setItem(j, ItemWorker.SetName(new ItemStack(Material.GREEN_WOOL),
                            ChangeWorker.Denomination.get(j) + "$"));
                }
                break;
            }
        }

        var cashInInventory = new Cash().AmountOfCashInInventory(player);
        // Кнопки внесения
        for (var i = 0; i < 9; i++) {
            if (cashInInventory >= ChangeWorker.Denomination.get(i)) {
                for (var j = i; j < 9; j++) {
                    window.setItem(j + 18, ItemWorker.SetName(new ItemStack(Material.RED_WOOL),
                            "-" + ChangeWorker.Denomination.get(j) + "$"));
                }
                break;
            }
        }

        return window;
    }
}
