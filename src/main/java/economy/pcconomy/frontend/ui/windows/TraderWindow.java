package economy.pcconomy.frontend.ui.windows;

import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.trade.npc.Trader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TraderWindow {
    public static Inventory GetTraderWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, "Торговец " + trader.getNPC().getFullName());

        for (ItemStack item:
                trader.Storage) {
            window.addItem(item);
        }

        return window;
    }

    public static Inventory GetOwnerTraderWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, "Торговец-Управление " + trader.getNPC().getFullName());

        window.setItem(0, ItemWorker.SetName(new ItemStack(Material.RED_WOOL), "Перейти в товары"));
        window.setItem(1, ItemWorker.SetName(new ItemStack(Material.RED_WOOL), "Забрать все товары"));
        window.setItem(2, ItemWorker.SetName(new ItemStack(Material.RED_WOOL), "Забрать прибыль"));

        return window;
    }

    public static Inventory GetRanterWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, "Торговец-Аренда " + trader.getNPC().getFullName());

        window.setItem(0, ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.RED_WOOL),
                "Арендовать"), trader.Cost + "$"));
        window.setItem(1, ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.RED_WOOL),
                "Процент города:"), trader.Revenue + "%"));

        return window;
    }

    public static Inventory GetMayorWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, "Торговец-Владелец " + trader.getNPC().getFullName());

        window.setItem(0, ItemWorker.SetName(new ItemStack(Material.RED_WOOL), "Установить цену"));

        return window;
    }

    public static Inventory GetAcceptWindow(Player player, ItemStack item) {
        var window = Bukkit.createInventory(player, 9, "Покупка");

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
