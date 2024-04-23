package economy.pcconomy.frontend.ui.windows.trade;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.npc.traits.Trader;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.Button;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;
import org.j1sk1ss.menuframework.objects.interactive.components.Slider;

import java.util.Arrays;
import java.util.Objects;

import static economy.pcconomy.frontend.ui.windows.trade.TraderListener.getTraderFromTitle;
import static economy.pcconomy.frontend.ui.windows.trade.TraderListener.rantTrader;


@ExtensionMethod({ItemManager.class})
public class TraderWindow {
        @SuppressWarnings("deprecation")
        public static MenuWindow TraderMenu =
            new MenuWindow(Arrays.asList(
                new Panel(Arrays.asList(
                    new Button(0, 20, "Перейти в товары", "Перейти в товары торговца",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader != null) player.openInventory(TraderWindow.getWindow(player, trader));
                        }),

                    new Button(3, 23, "Забрать все товары", "Забрать выставленные на продажу товары",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                trader.Storage.giveItemsWithoutLore(player);
                                trader.Storage.clear();
                            }
                        }),

                    new Button(6, 26, "Забрать прибыль", "Забрать заработанную прибыль",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                CashManager.giveCashToPlayer(trader.Revenue, player, false);
                                trader.Revenue = 0;
                            }
                        })
                ), "Торговец-Управление"),

                new Panel(Arrays.asList(
                    new Button(0, 21, "Арендовать на один день", "",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                var playerTradeLicense = PcConomy.GlobalLicenseManager.getLicense(player.getUniqueId(), LicenseType.Trade);
                                if (playerTradeLicense == null) return;
                                if (!playerTradeLicense.isOverdue())
                                    player.openInventory(TraderWindow.getExtendedRantedWindow(player, trader));
                            }
                        }),

                    new Button(5, 26, "НДС города:", "")
                ), "Торговец-Аренда"),

                new Panel(Arrays.asList(
                    new Button(0, 20, "Установить цену", "Установить цену аренды за 1 день",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);
                            if (trader != null) player.openInventory(TraderWindow.getPricesWindow(player, trader));
                        }),

                    new Button(3, 23, "Установить процент", "Установить процент с прибыли торговца",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);
                            if (trader != null) player.openInventory(TraderWindow.getMarginWindow(player, trader));
                        }),

                    new Button(6, 26, "Занять", "Занять торговца бесплатно",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);
                            var playerTradeLicense = PcConomy.GlobalLicenseManager.getLicense(player.getUniqueId(), LicenseType.Trade);

                            if (trader == null) return;
                            if (playerTradeLicense == null) return;
                            if (!playerTradeLicense.isOverdue()) rantTrader(trader, 1, player);
                        })
                ), "Торговец-Владелец"),

                new Panel(Arrays.asList(
                    new Slider(Arrays.asList(
                        0, 1, 2, 3, 4, 5, 6, 7, 8
                    ), Arrays.asList(
                        "100" + CashManager.currencySigh,  "500" + CashManager.currencySigh,   "1000" + CashManager.currencySigh,
                        "1500" + CashManager.currencySigh, "2000" + CashManager.currencySigh,  "2500" + CashManager.currencySigh,
                        "5000" + CashManager.currencySigh, "10000" + CashManager.currencySigh, "20000" + CashManager.currencySigh
                    ), "", "Slider", null),

                    new Button(9, 21, "Установить", "Установить выбранные цены",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader == null) return;
                            var slider = new Slider(TraderWindow.TraderMenu.getPanel("Торговец-Цена").getSliders().get(0), event.getInventory());
                            if (slider.getChose(event).equals("none")) return;

                            trader.Cost = Double.parseDouble(slider.getChose(event).replace(CashManager.currencySigh, ""));
                            player.sendMessage("Цена установлена!");
                        }),

                    new Button(14, 26, "Отмена", "",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            player.closeInventory();
                        })
                ), "Торговец-Цена"),

                new Panel(Arrays.asList(
                    new Slider(Arrays.asList(
                            0, 1, 2, 3, 4, 5, 6, 7, 8
                    ), Arrays.asList(
                            "5%", "10%", "20%", "30%", "40%", "50%", "60%", "90%", "100%"
                    ), "", "Slider", null),

                    new Button(9, 21, "Установить", "Установить процент наценки товаров",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);
                            if (trader == null) return;

                            var slider = new Slider(TraderWindow.TraderMenu.getPanel("Торговец-Процент").getSliders("Slider"), event.getInventory());
                            if (slider.getChose(event).equals("none")) return;

                            trader.Margin = Double.parseDouble(slider.getChose(event).replace("%", ""));
                            player.sendMessage("Процент установлен!");
                        }),

                    new Button(14, 26, "Отмена", "",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            player.closeInventory();
                        })
                ), "Торговец-Процент"),

                new Panel(Arrays.asList(
                    new Button(0, 21, "Купить", "",
                        (event) -> {
                            var player     = (Player)event.getWhoClicked();
                            var title      = event.getView().getTitle();
                            var trader     = getTraderFromTitle(title);
                            var inventory  = event.getInventory();
                            var buyingItem = inventory.getItem(13);
                            var price      = buyingItem.getPriceFromLore(0);

                            if (trader == null) return;
                            if (CashManager.amountOfCashInInventory(player, false) >= price || trader.Owner.equals(player.getUniqueId())) {
                                if (trader.Storage.contains(buyingItem)) {
                                    trader.Storage.remove(buyingItem);
                                    buyingItem.giveItemsWithoutLore(player);

                                    if (!trader.Owner.equals(player.getUniqueId())) {
                                        CashManager.takeCashFromPlayer(price, player, false);

                                        var endPrice = price / (1 + trader.Margin);
                                        PcConomy.GlobalTownManager.getTown(trader.HomeTown).changeBudget(price - endPrice);
                                        trader.Revenue += endPrice;

                                        if (TownyAPI.getInstance().getTown(player) != null)
                                            if (trader.SpecialList.contains(Objects.requireNonNull(TownyAPI.getInstance().getTown(player)).getUUID())) {
                                                    CashManager.giveCashToPlayer(price - endPrice, player, false);
                                                    PcConomy.GlobalTownManager.getTown(trader.HomeTown).changeBudget(-(price - endPrice));
                                                    player.sendMessage("Так как вы состоите в торговом союзе, пошлина была " +
                                                            "компенсированна городом");
                                            }
                                    }
                                }
                            }

                            player.openInventory(TraderWindow.getWindow(player, trader));
                        }),

                        new Button(5, 26, "Отмена", "",
                            (event) -> {
                                var player = (Player)event.getWhoClicked();
                                var title  = event.getView().getTitle();
                                var trader = getTraderFromTitle(title);

                                if (trader != null) player.openInventory(TraderWindow.getWindow(player, trader));
                            })
                ), "Торговец-Покупка")
            ));

    public static Inventory getWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, Component.text("Торговец-Ассортимент " + trader.getNPC().getId()));
        for (var i = 0; i < trader.Storage.size(); i++)
            window.setItem(i, trader.Storage.get(i));

        return window;
    }

    public static Inventory getOwnerWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, Component.text("Торговец-Управление " + trader.getNPC().getId()));
        TraderMenu.getPanel("Торговец-Управление").place(window,
                Arrays.asList(
                        "", trader.Storage.size() + " шт.", trader.Revenue + CashManager.currencySigh
                ));

        return window;
    }

    public static Inventory getRanterWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, Component.text("Торговец-Аренда " + trader.getNPC().getId()));
        TraderMenu.getPanel("Торговец-Аренда").place(window, Arrays.asList(
                "", trader.Margin + "%"
        ));

        return window;
    }

    public static Inventory getExtendedRantedWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 9, Component.text("Торговец-Аренда-Время " + trader.getNPC().getId()));
        for (var i = 0; i < 9; i++) //TODO: DATA MODEL
            window.setItem(i, new Item((i + 1) + " дней", trader.Cost * (i + 1) + CashManager.currencySigh, Material.PAPER, 1, 17000));

        return window;
    }

    public static Inventory getMayorWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, Component.text("Торговец-Владелец " + trader.getNPC().getId()));
        TraderMenu.getPanel("Торговец-Владелец").place(window);

        return window;
    }

    public static Inventory getPricesWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, Component.text("Торговец-Цена " + trader.getNPC().getId()));
        TraderMenu.getPanel("Торговец-Цена").place(window);

        return window;
    }

    public static Inventory getMarginWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, Component.text("Торговец-Процент " + trader.getNPC().getId()));
        TraderMenu.getPanel("Торговец-Процент").place(window);

        return window;
    }

    public static Inventory getAcceptWindow(Player player, ItemStack item, Trader trader) {
        var window = Bukkit.createInventory(player, 27, Component.text("Торговец-Покупка " + trader.getNPC().getId()));
        window.setItem(13, item);
        TraderMenu.getPanel("Торговец-Покупка").place(window);

        return window;
    }
}