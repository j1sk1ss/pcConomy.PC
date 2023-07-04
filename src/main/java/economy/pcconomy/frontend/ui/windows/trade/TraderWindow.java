package economy.pcconomy.frontend.ui.windows.trade;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.ItemManager;
import economy.pcconomy.backend.npc.traits.Trader;
import economy.pcconomy.frontend.ui.objects.Panel;
import economy.pcconomy.frontend.ui.objects.interactive.Button;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TraderWindow {
    public static Inventory getWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27,
                Component.text("Торговец-Покупка " + trader.getNPC().getId()));

        for (var i = 0; i < trader.Storage.size(); i++)
            window.setItem(i, trader.Storage.get(i));

        return window;
    }

    public static economy.pcconomy.frontend.ui.objects.Panel OwnerPanel = new Panel(Arrays.asList(
            new Button(Arrays.asList(
                    0, 1, 2, 9, 10, 11, 18, 19, 20
            ), "Перейти в товары", ""),
            new Button(Arrays.asList(
                    3, 4, 5, 12, 13, 14, 21, 22, 23
            ), "Забрать все товары", ""),
            new Button(Arrays.asList(
                    6, 7, 8, 15, 16, 17, 24, 25, 26
            ), "Забрать прибыль", "")
    ));

    public static Inventory getOwnerWindow(Player player, Trader trader) {
        return OwnerPanel.placeComponents(Bukkit.createInventory(player, 27,
                Component.text("Торговец-Управление " + trader.getNPC().getId())));
    }

    public static economy.pcconomy.frontend.ui.objects.Panel RantedPanel = new Panel(Arrays.asList(
            new Button(Arrays.asList(
                    0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21
            ), "Арендовать на один день", ""),
            new Button(Arrays.asList(
                    5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26
            ), "НДС города:", "")
    ));

    public static Inventory getRanterWindow(Player player, Trader trader) {
        return RantedPanel.placeComponents(Bukkit.createInventory(player, 27,
                Component.text("Торговец-Аренда " + trader.getNPC().getId())));
    }

    public static economy.pcconomy.frontend.ui.objects.Panel MayorPanel = new Panel(Arrays.asList(
            new Button(Arrays.asList(
                    0, 1, 2, 9, 10, 11, 18, 19, 20
            ), "Установить цену", ""),
            new Button(Arrays.asList(
                    3, 4, 5, 12, 13, 14, 21, 22, 23
            ), "Установить процент", ""),
            new Button(Arrays.asList(
                    6, 7, 8, 15, 16, 17, 24, 25, 26
            ), "Занять", "")
    ));

    public static Inventory getMayorWindow(Player player, Trader trader) {
        return MayorPanel.placeComponents(Bukkit.createInventory(player, 27,
                Component.text("Торговец-Владелец " + trader.getNPC().getId())));
    }

    public static Inventory getPricesWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 9,
                Component.text("Торговец-Цена " + trader.getNPC().getId()));

        for (var i = 0; i < 9; i++) {
            window.setItem(i, ItemManager.setName(new ItemStack(Material.GREEN_WOOL),
                    (i + 1) * 200 + CashManager.currencySigh));
        }

        return window;
    }

    public static Inventory getMarginWindow(Player player, Trader trader) { // TODO: Slider
        var window = Bukkit.createInventory(player, 9,
                Component.text("Торговец-Процент " + trader.getNPC().getId()));

        for (var i = 0; i < 9; i++)
            window.setItem(i, ItemManager.setName(new ItemStack(Material.GREEN_WOOL), (i + 1) * 5 + "%"));

        return window;
    }

    public static economy.pcconomy.frontend.ui.objects.Panel AcceptPanel = new Panel(Arrays.asList(
            new Button(Arrays.asList(
                    0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21
            ), "Купить", ""),
            new Button(Arrays.asList(
                    5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26
            ), "Отмена", "")
    ));

    public static Inventory getAcceptWindow(Player player, ItemStack item, Trader trader) {
        var window = Bukkit.createInventory(player, 27,
                Component.text("Покупка " + trader.getNPC().getId()));
        window.setItem(13, item);

        return AcceptPanel.placeComponents(window);
    }
}