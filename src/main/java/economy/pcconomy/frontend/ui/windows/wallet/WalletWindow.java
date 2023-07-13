package economy.pcconomy.frontend.ui.windows.wallet;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.cash.items.Wallet;
import economy.pcconomy.backend.scripts.items.Item;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WalletWindow {
    public static Inventory putWindow(Player player, ItemStack wallet) {
        var window = Bukkit.createInventory(player, 9, Component.text("Кошелёк"));
        var cashInInventory = CashManager.amountOfCashInInventory(player, true);

        for (var i = 0; i < 8; i++) {
            if (Wallet.getWalletAmount(wallet) > Wallet.WalletCapacity) break;
            if (i == 0)
                window.setItem(0, new Item("Положить все средства", //TODO: DATA MODEL
                        "-" + cashInInventory + CashManager.currencySigh, Material.PAPER, 1, 17000));

            if (cashInInventory >= CashManager.Denomination.get(i))
                printButtons("-", window);
        }

        return window;
    }

    public static Inventory withdrawWindow(Player player, ItemStack wallet) {
        var window = Bukkit.createInventory(player, 9, Component.text("Кошелёк"));
        var cashInWallet = Wallet.getWalletAmount(wallet);

        for (var i = 0; i < 8; i++) {
            if (i == 0)
                window.setItem(0, new Item("Снять максимум", //TODO: DATA MODEL
                        Math.round(cashInWallet * 100) / 100 + CashManager.currencySigh, Material.PAPER, 1, 17000));

            if (cashInWallet >= CashManager.Denomination.get(i))
                printButtons("", window);
        }

        return window;
    }

    private static void printButtons(String thing, Inventory window) {
        for (var j = 1; j < 8; j++) //TODO: DATA MODEL
            window.setItem(j, new Item("Действия",
                    thing + CashManager.Denomination.get(j) + CashManager.currencySigh,
                    Material.PAPER, 1, 17000));
    }
}
