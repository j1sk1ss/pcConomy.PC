package economy.pcconomy.frontend;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.npc.traits.Trader;
import economy.pcconomy.backend.economy.license.objects.LicenseType;

import lombok.experimental.ExtensionMethod;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.itemmanager.manager.Manager;
import net.potolotcraft.gorodki.GorodkiUniverse;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.nonInteractive.Margin;
import org.j1sk1ss.menuframework.objects.interactive.components.*;
import org.j1sk1ss.menuframework.objects.nonInteractive.Direction;

import java.util.*;
import java.time.LocalDateTime;


@ExtensionMethod({Manager.class, Cash.class})
public class TraderWindow {
        private static final MenuWindow TraderMenu =
            new MenuWindow(Arrays.asList(
                new Panel(List.of(
                    new ClickArea(new Margin(0, 0, 5, 8),
                        (event, menu) -> {
                            var player = (Player) event.getWhoClicked();
                            var title = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);
                            if (trader == null) return;

                            var choseItem = event.getInventory().getItem(event.getSlot());
                            if (choseItem == null) return;

                            if (!player.getInventory().contains(choseItem))
                                TraderWindow.getAcceptWindow(player, choseItem, trader);
                        })
                ), "Торговец-Ассортимент", MenuSizes.ThreeLines),

                new Panel(Arrays.asList(
                    new Button(new Margin(0, 0, 2, 1), "Перейти в товары", "Перейти в товары торговца",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader != null) getWindow(player, trader);
                        }, Material.GOLD_INGOT, 7000),

                    new Button(new Margin(0, 2, 2, 1), "Забрать все товары", "Забрать выставленные на продажу товары",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                trader.getStorage().giveItemsWithoutLore(player);
                                trader.getStorage().clear();
                            }
                        }, Material.GOLD_INGOT, 7000),

                    new Button(new Margin(0, 4, 2, 1), "Забрать прибыль", "Забрать заработанную прибыль",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                player.giveCashToPlayer(trader.getRevenue(), false);
                                trader.setRevenue(0);
                            }
                        }, Material.GOLD_INGOT, 7000),

                    new Button(new Margin(0, 7, 2, 1), "Окончить аренду", "Окончить аренду\nПрибыль и товары будут возвращены",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                trader.setRanted(false);
                                trader.setOwner(null);
                                trader.setTerm(LocalDateTime.now().toString());

                                trader.getStorage().giveItemsWithoutLore(player);
                                player.giveCashToPlayer(trader.getRevenue(), false);
                                trader.getStorage().clear();
                            }

                            player.closeInventory();
                        }, Material.GOLD_INGOT, 7000)
                ), "Торговец-Управление", MenuSizes.ThreeLines, "\u10E2"),

                new Panel(Arrays.asList(
                    new Button(new Margin(0, 0, 2, 3), "Арендовать", "",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                var playerTradeLicense = PcConomy.getInstance().licenseManager.getLicense(player.getUniqueId(), LicenseType.Trade);
                                if (playerTradeLicense == null) return;
                                if (!playerTradeLicense.isOverdue())
                                    getExtendedRantedWindow(player, trader);
                            }
                        }, Material.GOLD_INGOT, 7000),

                    new Button(new Margin(0, 5, 2, 3), "НДС города", "",
                        null, Material.GOLD_INGOT, 7000)
                ), "Торговец-Аренда", MenuSizes.ThreeLines, "\u10E3"),

                new Panel(List.of(
                    new ClickArea(new Margin(0, 0, 0, 8),
                        (event, menu) -> {
                            var player = (Player) event.getWhoClicked();
                            var title = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);
                            if (trader == null) return;

                            var choseItem = event.getCurrentItem();
                            if (choseItem == null) return;

                            var days = Integer.parseInt(choseItem.getName().split(" ")[0]);
                            if (player.amountOfCashInInventory(false) < trader.getCost() * days) return;
                            player.takeCashFromPlayer(trader.getCost() * days, false);
                            GorodkiUniverse.getInstance().getGorod(trader.getHomeTown()).changeBudget(trader.getCost() * days);

                            rantTrader(trader, days, player);
                            player.closeInventory();
                        })
                ), "Торговец-Время-Аренда", MenuSizes.OneLine, "\u10DF"),

                new Panel(Arrays.asList(
                    new Button(new Margin(0, 0, 2, 2), "Установить цену", "Установить цену аренды за 1 день",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);
                            if (trader != null) getPricesWindow(player, trader);
                        }, Material.GOLD_INGOT, 7000),

                    new Button(new Margin(0, 3, 2, 2), "Установить процент", "Установить процент с прибыли торговца",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);
                            if (trader != null) getMarginWindow(player, trader);
                        }, Material.GOLD_INGOT, 7000),

                    new Button(new Margin(0, 6, 2, 2), "Занять", "Занять торговца бесплатно",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader == null) return;
                            rantTrader(trader, 1, player);
                            player.sendMessage("Торговец успешно занят!");
                        }, Material.GOLD_INGOT, 7000)
                ), "Торговец-Владелец", MenuSizes.ThreeLines, "\u10DC"),

                new Panel(Arrays.asList(
                    new Slider(new Margin(0, 0, 8, Direction.Horizontal), Arrays.asList(
                        "100" + Cash.currencySigh,  "500" + Cash.currencySigh,   "1000" + Cash.currencySigh,
                        "1500" + Cash.currencySigh, "2000" + Cash.currencySigh,  "2500" + Cash.currencySigh,
                        "5000" + Cash.currencySigh, "10000" + Cash.currencySigh, "20000" + Cash.currencySigh
                    ), "", "Цена аренды", null, 17000, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT),

                    new Button(new Margin(1, 5, 1, 3), "Установить", "Установить выбранные цены",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader == null) return;
                            var price = TraderWindow.TraderMenu.getPanel("Торговец-Цена").getComponent("Цена аренды", Slider.class).getChose(event);
                            if (price.equals(Slider.SliderNone)) return;

                            trader.setCost(Double.parseDouble(price.replace(Cash.currencySigh, "")));
                            player.sendMessage("Цена установлена!");
                        }, Material.GOLD_INGOT, 7000),

                    new Button(new Margin(1, 0, 1, 3), "Отмена", "",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            player.closeInventory();
                        }, Material.GOLD_INGOT, 7000)
                ), "Торговец-Цена", MenuSizes.ThreeLines, "\u10DD"),

                new Panel(Arrays.asList(
                    new Slider(new Margin(0, 0, 8, Direction.Horizontal), Arrays.asList(
                            "5%", "10%", "20%", "30%", "40%", "50%", "60%", "90%", "100%"
                    ), "", "Процент города", null, 17000, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT),

                    new Button(new Margin(1, 5, 1, 3), "Установить", "Установить процент наценки товаров",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);
                            if (trader == null) return;

                            var percent = TraderWindow.TraderMenu.getPanel("Торговец-Процент").getComponent("Процент города", Slider.class).getChose(event);
                            if (percent.equals(Slider.SliderNone)) return;

                            trader.setMargin(Double.parseDouble(percent.replace("%", "")) / 100);
                            player.sendMessage("Процент установлен!");
                        }, Material.GOLD_INGOT, 7000),

                    new Button(new Margin(1, 0, 1, 3), "Отмена", "",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            player.closeInventory();
                        }, Material.GOLD_INGOT, 7000)
                ), "Торговец-Процент", MenuSizes.ThreeLines, "\u10DD"),

                new Panel(Arrays.asList(
                    new Button(new Margin(0, 0, 2, 3), "Купить", "",
                        (event, menu) -> {
                            var player     = (Player)event.getWhoClicked();
                            var title      = Utils.getInventoryTitle(event);
                            var trader     = getTraderFromTitle(title);
                            var inventory  = event.getInventory();
                            var buyingItem = inventory.getItem(13);
                            var price = Objects.requireNonNull(buyingItem).getDoubleFromContainer("item-price");

                            if (trader == null) return;
                            if (player.amountOfCashInInventory(false) >= price || trader.getOwner().equals(player.getUniqueId())) {
                                if (trader.getStorage().contains(buyingItem)) {
                                    trader.getStorage().remove(buyingItem);
                                    buyingItem.giveItemsWithoutLore(player);

                                    if (!trader.getOwner().equals(player.getUniqueId())) {
                                        player.takeCashFromPlayer(price, false);
                                        var endPrice = price / (1 + trader.getMargin());
                                        var town = GorodkiUniverse.getInstance().getGorod(trader.getHomeTown());
                                        if (town != null) {
                                            town.changeBudget(price - endPrice);
                                            trader.setRevenue(trader.getRevenue() + endPrice);

                                            if (TownyAPI.getInstance().getTown(player) != null)
                                                if (trader.getSpecialList().contains(Objects.requireNonNull(TownyAPI.getInstance().getTown(player)).getUUID())) {
                                                    player.giveCashToPlayer(price - endPrice, false);
                                                    GorodkiUniverse.getInstance().getGorod(trader.getHomeTown()).changeBudget(-(price - endPrice));
                                                    player.sendMessage("Так как вы состоите в торговом союзе, пошлина была компенсированна городом");
                                                }
                                        }
                                        else {
                                            player.sendMessage("Что-то пошло не так. Города... нету.");
                                        }
                                    }
                                }
                            }
                            else player.sendMessage("У вас нет денег, милорд.");

                            getWindow(player, trader);
                        }, Material.GOLD_INGOT, 7000),

                    new Button(new Margin(0, 5, 2, 3), "Отмена", "",
                        (event, menu) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader != null) getWindow(player, trader);
                        }, Material.GOLD_INGOT, 7000)
                ), "Торговец-Покупка", MenuSizes.ThreeLines, "\u10DB")
            ), "Trader");

    public static void getWindow(Player player, Trader trader) {
        TraderWindow.TraderMenu.getPanel("Торговец-Ассортимент", PcConomy.getInstance().config.getString("ui.language", "RU")).resize(9 * trader.getLevel())
            .getViewWith(
                player,
                "Торговец-Ассортимент " + trader.getNPC().getId(),
                List.of(new ItemArea(new Margin(0, 0, trader.getLevel(), 8), trader.getStorage(), null))
            );
    }

    public static void getOwnerWindow(Player player, Trader trader) {
        TraderWindow.TraderMenu.getPanel("Торговец-Управление", PcConomy.getInstance().config.getString("ui.language", "RU"))
                .getView(player, "Торговец-Управление " + trader.getNPC().getId());
    }

    public static void getRanterWindow(Player player, Trader trader) {
        TraderMenu.getPanel("Торговец-Аренда", PcConomy.getInstance().config.getString("ui.language", "RU"))
            .getView(player, "Торговец-Аренда " + trader.getNPC().getId(),
                Map.of(
                "Арендовать", List.of("Окно аренды торговца. Цена за день: " + trader.getCost() + Cash.currencySigh
                ),
                "НДС города", List.of(trader.getMargin() * 100 + "%")
                )
            );
    }

    public static void getMayorWindow(Player player, Trader trader) {
        TraderMenu.getPanel("Торговец-Владелец").getView(player, "Торговец-Владелец " + trader.getNPC().getId());
    }

    public static Trader getTraderFromTitle(String name) {
        try {
            if (Arrays.stream(name.split(" ")).toList().size() <= 1) return null;
            return NpcManager.getNPC(Integer.parseInt(name.split(" ")[1])).getOrAddTrait(Trader.class);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static void getExtendedRantedWindow(Player player, Trader trader) {
        var components = new ArrayList<org.j1sk1ss.menuframework.objects.interactive.Component>();
        for (var i = 0; i < 9; i++)
            components.add(
                    new LittleButton(new Margin(i, 0, 0),
                            (i + 1) + " дней",
                            trader.getCost() * (i + 1) + Cash.currencySigh,
                            null, Material.GOLD_INGOT, 7000)
            );

        TraderMenu.getPanel("Торговец-Время-Аренда").getViewWith(
                player,
                "Торговец-Время-Аренда " + trader.getNPC().getId(),
                components);
    }

    private static void getPricesWindow(Player player, Trader trader) {
        TraderMenu.getPanel("Торговец-Цена").getView(player, "Торговец-Цена " + trader.getNPC().getId());
    }

    private static void getMarginWindow(Player player, Trader trader) {
        TraderMenu.getPanel("Торговец-Процент").getView(player, "Торговец-Процент " + trader.getNPC().getId());
    }

    private static void getAcceptWindow(Player player, ItemStack item, Trader trader) {
        TraderMenu.getPanel("Торговец-Покупка").getViewWith(
            player,
            "Торговец-Покупка " + trader.getNPC().getId(),
            List.of(new Icon(new Margin(13, 0, 0), item))
        );
    }

    private static void rantTrader(Trader trader, int days, Player ranter) {
        trader.setOwner(ranter.getUniqueId());
        trader.setRanted(true);
        trader.setTerm(LocalDateTime.now().plusDays(days).toString());
    }
}