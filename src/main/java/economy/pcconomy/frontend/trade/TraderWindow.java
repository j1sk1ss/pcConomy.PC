package economy.pcconomy.frontend.trade;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.economy.town.manager.TownManager;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.npc.traits.Trader;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.Button;
import org.j1sk1ss.menuframework.objects.interactive.components.ClickArea;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;
import org.j1sk1ss.menuframework.objects.interactive.components.Slider;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@ExtensionMethod({Manager.class, CashManager.class, TownManager.class})
public class TraderWindow {
        @SuppressWarnings("deprecation")
        public static MenuWindow TraderMenu =
            new MenuWindow(Arrays.asList(
                new Panel(List.of(
                    new ClickArea(0, 53,
                        (event) -> {
                            var player = (Player) event.getWhoClicked();
                            var title = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);
                            if (trader == null) return;

                            var choseItem = event.getCurrentItem();
                            if (choseItem == null) return;

                            if (!player.getInventory().contains(choseItem))
                                player.openInventory(TraderWindow.getAcceptWindow(player, choseItem, trader));
                        })
                ), "Торговец-Ассортимент"),

                new Panel(Arrays.asList(
                    new Button(0, 19, "Перейти в товары", "Перейти в товары торговца",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader != null) player.openInventory(getWindow(player, trader));
                        }),

                    new Button(2, 21, "Забрать все товары", "Забрать выставленные на продажу товары",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                trader.Storage.giveItemsWithoutLore(player);
                                trader.Storage.clear();
                            }
                        }),

                    new Button(4, 23, "Забрать прибыль", "Забрать заработанную прибыль",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                player.giveCashToPlayer(trader.Revenue, false);
                                trader.Revenue = 0;
                            }
                        }),

                    new Button(6, 26, "Окончить аренду", "Окончить аренду\nПрибыль и товары будут возвращены",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                trader.IsRanted = false;
                                trader.Owner    = null;
                                trader.Term     = LocalDateTime.now().toString();;

                                trader.Storage.giveItemsWithoutLore(player);
                                player.giveCashToPlayer(trader.Revenue, false);
                                trader.Storage.clear();
                            }
                        })
                ), "Торговец-Управление"),

                new Panel(Arrays.asList(
                    new Button(0, 21, "Арендовать", "",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                var playerTradeLicense = PcConomy.GlobalLicenseManager.getLicense(player.getUniqueId(), LicenseType.Trade);
                                if (playerTradeLicense == null) return;
                                if (!playerTradeLicense.isOverdue())
                                    player.openInventory(getExtendedRantedWindow(player, trader));
                            }
                        }),

                    new Button(5, 26, "НДС города:", "")
                ), "Торговец-Аренда"),

                new Panel(List.of(
                    new ClickArea(0, 8,
                        (event) -> {
                            var player = (Player) event.getWhoClicked();
                            var title = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);
                            if (trader == null) return;

                            var choseItem = event.getCurrentItem();
                            if (choseItem == null) return;

                            var days = Integer.parseInt(choseItem.getName().split(" ")[0]);
                            if (player.amountOfCashInInventory(false) < trader.Cost * days) return;
                            player.takeCashFromPlayer(trader.Cost * days, false);
                            trader.HomeTown.getTown().changeBudget(trader.Cost * days);

                            rantTrader(trader, days, player);
                            player.closeInventory();
                        })
                ), "Торговец-Аренда-Время", MenuSizes.OneLine),

                new Panel(Arrays.asList(
                    new Button(0, 20, "Установить цену", "Установить цену аренды за 1 день",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);
                            if (trader != null) player.openInventory(getPricesWindow(player, trader));
                        }),

                    new Button(3, 23, "Установить процент", "Установить процент с прибыли торговца",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);
                            if (trader != null) player.openInventory(getMarginWindow(player, trader));
                        }),

                    new Button(6, 26, "Занять", "Занять торговца бесплатно",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader == null) return;
                            rantTrader(trader, 1, player);
                            player.sendMessage("Торговец успешно занят!");
                        })
                ), "Торговец-Владелец"),

                new Panel(Arrays.asList(
                    new Slider(Arrays.asList(
                        0, 1, 2, 3, 4, 5, 6, 7, 8
                    ), Arrays.asList(
                        "100" + CashManager.currencySigh,  "500" + CashManager.currencySigh,   "1000" + CashManager.currencySigh,
                        "1500" + CashManager.currencySigh, "2000" + CashManager.currencySigh,  "2500" + CashManager.currencySigh,
                        "5000" + CashManager.currencySigh, "10000" + CashManager.currencySigh, "20000" + CashManager.currencySigh
                    ), "", "Цена аренды", null),

                    new Button(9, 21, "Установить", "Установить выбранные цены",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);

                            if (trader == null) return;
                            var price = TraderWindow.TraderMenu.getPanel("Торговец-Цена").getSliders("Цена аренды").getChose(event);
                            if (price.equals("none")) return;

                            trader.Cost = Double.parseDouble(price.replace(CashManager.currencySigh, ""));
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
                    ), "", "Процент города", null),

                    new Button(9, 21, "Установить", "Установить процент наценки товаров",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = event.getView().getTitle();
                            var trader = getTraderFromTitle(title);
                            if (trader == null) return;

                            var percent = TraderWindow.TraderMenu.getPanel("Торговец-Процент").getSliders("Процент города").getChose(event);
                            if (percent.equals("none")) return;

                            trader.Margin = Double.parseDouble(percent.replace("%", "")) / 100;
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
                            assert buyingItem != null;

                            var price = buyingItem.getDoubleFromContainer("item-price");

                            if (trader == null) return;
                            if (player.amountOfCashInInventory(false) >= price || trader.Owner.equals(player.getUniqueId())) {
                                if (trader.Storage.contains(buyingItem)) {
                                    trader.Storage.remove(buyingItem);
                                    buyingItem.giveItemsWithoutLore(player);

                                    if (!trader.Owner.equals(player.getUniqueId())) {
                                        player.takeCashFromPlayer(price, false);

                                        var endPrice = price / (1 + trader.Margin);
                                        trader.HomeTown.getTown().changeBudget(price - endPrice);
                                        trader.Revenue += endPrice;

                                        if (TownyAPI.getInstance().getTown(player) != null)
                                            if (trader.SpecialList.contains(Objects.requireNonNull(TownyAPI.getInstance().getTown(player)).getUUID())) {
                                                player.giveCashToPlayer(price - endPrice, false);
                                                trader.HomeTown.getTown().changeBudget(-(price - endPrice));
                                                    player.sendMessage("Так как вы состоите в торговом союзе, пошлина была компенсированна городом");
                                            }
                                    }
                                }
                            }

                            player.openInventory(getWindow(player, trader));
                        }),

                        new Button(5, 26, "Отмена", "",
                            (event) -> {
                                var player = (Player)event.getWhoClicked();
                                var title  = event.getView().getTitle();
                                var trader = getTraderFromTitle(title);

                                if (trader != null) player.openInventory(getWindow(player, trader));
                            })
                ), "Торговец-Покупка")
            ));

    public static Inventory getWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 9 * trader.Level, Component.text("Торговец-Ассортимент " + trader.getNPC().getId()));
        for (var i = 0; i < trader.Storage.size(); i++)
            window.setItem(i, trader.Storage.get(i));

        return window;
    }

    public static Inventory getOwnerWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, Component.text("Торговец-Управление " + trader.getNPC().getId()));
        TraderMenu.getPanel("Торговец-Управление").place(window);
        return window;
    }

    public static Inventory getRanterWindow(Player player, Trader trader) {
        var window = Bukkit.createInventory(player, 27, Component.text("Торговец-Аренда " + trader.getNPC().getId()));
        TraderMenu.getPanel("Торговец-Аренда").place(window, Arrays.asList(
            Arrays.asList(
                "Окно аренды торговца. Цена за день: ",
                trader.Cost + CashManager.currencySigh
            ),

            List.of(
                trader.Margin + "%"
            )
        ), Arrays.asList(
            "Арендовать", "НДС города:"
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

    public static Trader getTraderFromTitle(String name) {
        try {
            if (Arrays.stream(name.split(" ")).toList().size() <= 1) return null;
            return NpcManager.getNPC(Integer.parseInt(name.split(" ")[1])).getOrAddTrait(Trader.class);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static void rantTrader(Trader trader, int days, Player ranter) {
        trader.Owner    = ranter.getUniqueId();
        trader.IsRanted = true;
        trader.Term     = LocalDateTime.now().plusDays(days).toString();
    }
}