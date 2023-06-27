package economy.pcconomy.frontend.ui.windows.trade;

import economy.pcconomy.backend.cash.scripts.CashManager;
import economy.pcconomy.backend.scripts.ItemManager;
import economy.pcconomy.backend.trade.npc.Trader;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TraderWindow {
    public static Inventory GetWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27,
                Component.text("Торговец-Покупка " + trader.getNPC().getId()));

        for (var i = 0; i < trader.Storage.size(); i++) {
            window.setItem(i, trader.Storage.get(i));
        }

        return window;
    }

    public static Inventory GetOwnerWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27,
                Component.text("Торговец-Управление " + trader.getNPC().getId()));

        window.setItem(0, ItemManager.setName(new ItemStack(Material.RED_WOOL), "Перейти в товары"));
        window.setItem(1, ItemManager.setName(new ItemStack(Material.RED_WOOL), "Забрать все товары"));
        window.setItem(2, ItemManager.setName(new ItemStack(Material.RED_WOOL), "Забрать прибыль"));

        return window;
    }

    public static Inventory GetRanterWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27,
                Component.text("Торговец-Аренда " + trader.getNPC().getId()));

        window.setItem(0, ItemManager.setLore(ItemManager.setName(new ItemStack(Material.RED_WOOL),
                "Арендовать на один день"), trader.Cost + CashManager.currencySigh));
        window.setItem(1, ItemManager.setLore(ItemManager.setName(new ItemStack(Material.RED_WOOL),
                "НДС города: "), trader.Margin * 100 + "%"));

        return window;
    }

    public static Inventory GetMayorWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27,
                Component.text("Торговец-Владелец " + trader.getNPC().getId()));

        window.setItem(0, ItemManager.setName(new ItemStack(Material.RED_WOOL), "Установить цену"));
        window.setItem(1, ItemManager.setName(new ItemStack(Material.RED_WOOL), "Установить процент"));
        window.setItem(2, ItemManager.setName(new ItemStack(Material.RED_WOOL), "Занять"));

        return window;
    }

    public static Inventory GetPricesWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 9,
                Component.text("Торговец-Цена " + trader.getNPC().getId()));

        for (var i = 0; i < 9; i++) {
            window.setItem(i, ItemManager.setName(new ItemStack(Material.GREEN_WOOL),
                    (i + 1) * 200 + CashManager.currencySigh));
        }

        return window;
    }

    public static Inventory GetMarginWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 9,
                Component.text("Торговец-Процент " + trader.getNPC().getId()));

        for (var i = 0; i < 9; i++) {
            window.setItem(i, ItemManager.setName(new ItemStack(Material.GREEN_WOOL), (i + 1) * 5 + "%"));
        }

        return window;
    }

    public static Inventory GetAcceptWindow(Player player, ItemStack item, Trader trader) {
        var window = Bukkit.createInventory(player, 9,
                Component.text("Покупка " + trader.getNPC().getId()));

        for (var i = 0; i < 3; i++) {
            window.setItem(i, ItemManager.setName(new ItemStack(Material.RED_STAINED_GLASS_PANE), "ОТМЕНА"));
        }

        window.setItem(4, item);

        for (var i = 6; i < 9; i++) {
            window.setItem(i, ItemManager.setName(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "КУПИТЬ"));
        }

        return window;
    }
}
