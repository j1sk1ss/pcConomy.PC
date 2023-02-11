package economy.pcconomy.frontend.ui.windows.trade;

import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.scripts.TownWorker;
import economy.pcconomy.backend.trade.npc.Trader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TraderWindow {
    public static Inventory GetTraderWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, "Торговец-Покупка " + trader.getNPC().getId());

        for (ItemStack item:
                trader.Storage) {
            window.addItem(item);
        }

        return window;
    }

    public static Inventory GetOwnerTraderWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, "Торговец-Управление " + trader.getNPC().getId());

        window.setItem(0, ItemWorker.SetName(new ItemStack(Material.RED_WOOL), "Перейти в товары"));
        window.setItem(1, ItemWorker.SetName(new ItemStack(Material.RED_WOOL), "Забрать все товары"));
        window.setItem(2, ItemWorker.SetName(new ItemStack(Material.RED_WOOL), "Забрать прибыль"));

        return window;
    }

    public static Inventory GetRanterWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, "Торговец-Аренда " + trader.getNPC().getId());

        window.setItem(0, ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.RED_WOOL),
                "Арендовать"), trader.Cost + "$"));
        window.setItem(1, ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.RED_WOOL),
                "Процент:"), trader.Margin * 100 + "%"));

        return window;
    }

    public static Inventory GetMayorWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, "Торговец-Владелец " + trader.getNPC().getId());

        window.setItem(0, ItemWorker.SetName(new ItemStack(Material.RED_WOOL), "Установить цену"));
        window.setItem(1, ItemWorker.SetName(new ItemStack(Material.RED_WOOL), "Установить процент"));
        window.setItem(2, ItemWorker.SetName(new ItemStack(Material.RED_WOOL), "Занять"));

        return window;
    }

    public static Inventory GetPricesWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 9, "Торговец-Цена " + trader.getNPC().getId());

        for (var i = 0; i < 9; i++) {
            window.setItem(i, ItemWorker.SetName(new ItemStack(Material.GREEN_WOOL), (i + 1) * 200 + "$"));
        }

        return window;
    }

    public static Inventory GetMarginWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 9, "Торговец-Процент " + trader.getNPC().getId());

        for (var i = 0; i < 9; i++) {
            window.setItem(i, ItemWorker.SetName(new ItemStack(Material.GREEN_WOOL), (i + 1) * 5 + "%"));
        }

        return window;
    }

    public static Inventory GetAcceptWindow(Player player, ItemStack item, Trader trader) {
        var window = Bukkit.createInventory(player, 9, "Покупка " + trader.getNPC().getId());

        for (var i = 0; i < 3; i++) {
            window.setItem(i, ItemWorker.SetName(new ItemStack(Material.RED_STAINED_GLASS_PANE), "ОТМЕНА"));
        }

        window.setItem(4, item);

        for (var i = 6; i < 9; i++) {
            window.setItem(i, ItemWorker.SetName(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "КУПИТЬ"));
        }

        return window;
    }
}
