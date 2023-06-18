package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.scripts.CashManager;
import economy.pcconomy.backend.cash.scripts.ChangeWorker;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.frontend.ui.windows.IWindow;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BankerWindow implements IWindow {
    public Inventory generateWindow(Player player, boolean isNpc) {
        var window = Bukkit.createInventory(player, 27, Component.text("Банк"));

        var enableBalance   = PcConomy.GlobalBank.GetUsefulAmountOfBudget();
        var playerBalance   = new BalanceWorker().getBalance(player);
        var cashInInventory = new Cash().AmountOfCashInInventory(player);

        for (var i = 0; i < 9; i++) {
            if (i == 0 && playerBalance < enableBalance) {
                window.setItem(0, ItemWorker.SetName(new ItemStack(Material.GREEN_WOOL),
                        Math.round(playerBalance * 100) / 100 + CashManager.currencySigh));
                window.setItem(18, ItemWorker.SetName(new ItemStack(Material.RED_WOOL),
                        "-" + cashInInventory + CashManager.currencySigh));
            }

            if (enableBalance >= ChangeWorker.Denomination.get(i) && playerBalance >= ChangeWorker.Denomination.get(i))
                for (var j = i; j < 8; j++)
                    window.setItem(j + 1, ItemWorker.SetName(new ItemStack(Material.GREEN_WOOL),
                            ChangeWorker.Denomination.get(j) + CashManager.currencySigh));

            if (cashInInventory >= ChangeWorker.Denomination.get(i))
                for (var j = i; j < 8; j++)
                    window.setItem(j + 19, ItemWorker.SetName(new ItemStack(Material.RED_WOOL),
                            "-" + ChangeWorker.Denomination.get(j) + CashManager.currencySigh));
        }

        return window;
    }
}
