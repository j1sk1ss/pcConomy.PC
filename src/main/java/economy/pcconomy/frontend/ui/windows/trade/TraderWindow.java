package economy.pcconomy.frontend.ui.windows.trade;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.npc.traits.Trader;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.frontend.ui.objects.Menu;
import economy.pcconomy.frontend.ui.objects.Panel;
import economy.pcconomy.frontend.ui.objects.interactive.Button;
import economy.pcconomy.frontend.ui.objects.interactive.Slider;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TraderWindow {
    public static Menu TraderMenu = 
        new Menu(Arrays.asList(
                new Panel(Arrays.asList(
                        new Button(0, 20, "Перейти в товары", ""),
                        new Button(3, 23, "Забрать все товары", ""),
                        new Button(6, 26, "Забрать прибыль", "")
                ), "Торговец-Управление"),

                new Panel(Arrays.asList(
                        new Button(0, 21, "Арендовать на один день", ""),
                        new Button(5, 26, "НДС города:", "")
                ), "Торговец-Аренда"),

                new Panel(Arrays.asList(
                        new Button(0, 20, "Установить цену", ""),
                        new Button(3, 23, "Установить процент", ""),
                        new Button(6, 26, "Занять", "")
                ), "Торговец-Владелец"),

                new Panel(Arrays.asList(
                        new Slider(Arrays.asList(
                                0, 1, 2, 3, 4, 5, 6, 7, 8
                        ), Arrays.asList(
                                "100" + CashManager.currencySigh, "500" + CashManager.currencySigh, "1000" + CashManager.currencySigh,
                                "1500" + CashManager.currencySigh, "2000" + CashManager.currencySigh, "2500" + CashManager.currencySigh,
                                "5000" + CashManager.currencySigh, "10000" + CashManager.currencySigh, "20000" + CashManager.currencySigh
                        ), "", "Slider"),

                        new Button(9, 21, "Установить", ""),
                        new Button(14, 26, "Отмена", "")
                ), "Торговец-Цена"),

                new Panel(Arrays.asList(
                        new Slider(Arrays.asList(
                                0, 1, 2, 3, 4, 5, 6, 7, 8
                        ), Arrays.asList(
                                "5%", "10%", "20%", "30%", "40%", "50%", "60%", "90%", "100%"
                        ), "", "Slider"),
                        
                        new Button(9, 21, "Установить", ""),
                        new Button(14, 26, "Отмена", "")
                ), "Торговец-Процент"),

                new Panel(Arrays.asList(
                        new Button(0, 21, "Купить", ""),
                        new Button(5, 26, "Отмена", "")
                ), "Торговец-Покупка")
        ));

    public static Inventory getWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27,
                Component.text("Торговец-Ассортимент " + trader.getNPC().getId()));

        for (var i = 0; i < trader.Storage.size(); i++)
            window.setItem(i, trader.Storage.get(i));

        return window;
    }

    public static Inventory getOwnerWindow(Player player, Trader trader) {
        return TraderMenu.getPanel("Торговец-Управление").placeComponents(Bukkit.createInventory(player, 27,
                Component.text("Торговец-Управление " + trader.getNPC().getId())),
                Arrays.asList(
                        "", trader.Storage.size() + " шт.", trader.Revenue + CashManager.currencySigh
                ));
    }

    public static Inventory getRanterWindow(Player player, Trader trader) {
        return TraderMenu.getPanel("Торговец-Аренда").placeComponents(Bukkit.createInventory(player, 27,
                Component.text("Торговец-Аренда " + trader.getNPC().getId())), Arrays.asList(
                "", trader.Margin + "%"
        ));
    }

    public static Inventory getExtendedRantedWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 9,
                Component.text("Торговец-Аренда-Время " + trader.getNPC().getId()));

        for (var i = 0; i < 9; i++) //TODO: DATA MODEL
            window.setItem(i, new Item((i + 1) + " дней", trader.Cost * (i + 1) + CashManager.currencySigh,
                    Material.PAPER, 1, 17000));

        return window;
    }

    public static Inventory getMayorWindow(Player player, Trader trader) {
        return TraderMenu.getPanel("Торговец-Владелец").placeComponents(Bukkit.createInventory(player, 27,
                Component.text("Торговец-Владелец " + trader.getNPC().getId())));
    }

    public static Inventory getPricesWindow(Player player, Trader trader) {
        return TraderMenu.getPanel("Торговец-Цена").placeComponents(Bukkit.createInventory(player, 27,
                Component.text("Торговец-Цена " + trader.getNPC().getId())));
    }

    public static Inventory getMarginWindow(Player player, Trader trader) {
        return TraderMenu.getPanel("Торговец-Процент").placeComponents(Bukkit.createInventory(player, 27,
                Component.text("Торговец-Процент " + trader.getNPC().getId())));
    }

    public static Inventory getAcceptWindow(Player player, ItemStack item, Trader trader) {
        var window = Bukkit.createInventory(player, 27,
                Component.text("Торговец-Покупка " + trader.getNPC().getId()));
        window.setItem(13, item);

        return TraderMenu.getPanel("Торговец-Покупка").placeComponents(window);
    }
}