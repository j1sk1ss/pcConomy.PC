package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.cash.ChangeManager;
import economy.pcconomy.backend.scripts.BalanceManager;
import economy.pcconomy.backend.scripts.ItemManager;
import economy.pcconomy.frontend.ui.objects.Panel;
import economy.pcconomy.frontend.ui.objects.interactive.Button;
import economy.pcconomy.frontend.ui.windows.IWindow;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BankerWindow implements IWindow {
    public static final economy.pcconomy.frontend.ui.objects.Panel Panel = new Panel(Arrays.asList(
            new Button(Arrays.asList(
                    0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21
            ), "Внести деньги", ""),
            new Button(Arrays.asList(
                    5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26
            ), "Вывести деньги", "")
    ));

    public Inventory generateWindow(Player player) {
        return Panel.placeComponents(Bukkit.createInventory(player, 27, Component.text("Банк")));
    }

    public static Inventory withdrawWindow(Player player) {
        var window = Bukkit.createInventory(player, 9, Component.text("Банк-Снятие"));

        var enableBalance   = PcConomy.GlobalBank.getUsefulAmountOfBudget();
        var playerBalance   = new BalanceManager().getBalance(player);

        for (var i = 0; i < 9; i++) {
            if (i == 0 && playerBalance < enableBalance)
                window.setItem(0, ItemManager.setName(new ItemStack(Material.GREEN_WOOL),
                        Math.round(playerBalance * 100) / 100 + CashManager.currencySigh));

            if (enableBalance >= ChangeManager.Denomination.get(i) && playerBalance >= ChangeManager.Denomination.get(i))
                for (var j = i; j < 8; j++)
                    window.setItem(j + 1, ItemManager.setName(new ItemStack(Material.GREEN_WOOL),
                            ChangeManager.Denomination.get(j) + CashManager.currencySigh));
        }

        return window;
    }

    public static Inventory putWindow(Player player) {
        var window = Bukkit.createInventory(player, 9, Component.text("Банк-Внесение"));
        var cashInInventory = new CashManager().amountOfCashInInventory(player);

        for (var i = 0; i < 9; i++) {
            if (i == 0)
                window.setItem(0, ItemManager.setName(new ItemStack(Material.RED_WOOL),
                        "-" + cashInInventory + CashManager.currencySigh));

            if (cashInInventory >= ChangeManager.Denomination.get(i))
                for (var j = i; j < 8; j++)
                    window.setItem(j + 1, ItemManager.setName(new ItemStack(Material.RED_WOOL),
                            "-" + ChangeManager.Denomination.get(j) + CashManager.currencySigh));
        }

        return window;
    }
}
