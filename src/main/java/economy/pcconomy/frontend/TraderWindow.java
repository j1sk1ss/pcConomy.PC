package economy.pcconomy.frontend;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.economy.license.objects.LicenseType;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.npc.traits.Trader;

import lombok.experimental.ExtensionMethod;

import net.potolotcraft.gorodki.GorodkiUniverse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.itemmanager.manager.Manager;
import org.j1sk1ss.menuframework.common.LocalizationManager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@ExtensionMethod({Manager.class, Cash.class})
public class TraderWindow {
        public static MenuWindow TraderMenu =
            new MenuWindow(Arrays.asList(
                new Panel(List.of(
                    new ClickArea(0, 53,
                        (event) -> {
                            var player = (Player) event.getWhoClicked();
                            var title = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);
                            if (trader == null) return;

                            var choseItem = event.getCurrentItem();
                            if (choseItem == null) return;

                            if (!player.getInventory().contains(choseItem))
                                TraderWindow.getAcceptWindow(player, choseItem, trader);
                        })
                ), "Торговец-Ассортимент", MenuSizes.ThreeLines, "\u10DA"),

                new Panel(Arrays.asList(
                    new Button(0, 19, "Перейти в товары", "Перейти в товары торговца",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader != null) getWindow(player, trader);
                        }, Material.GOLD_INGOT, 7000),

                    new Button(2, 21, "Забрать все товары", "Забрать выставленные на продажу товары",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                trader.Storage.giveItemsWithoutLore(player);
                                trader.Storage.clear();
                            }
                        }, Material.GOLD_INGOT, 7000),

                    new Button(4, 23, "Забрать прибыль", "Забрать заработанную прибыль",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                player.giveCashToPlayer(trader.Revenue, false);
                                trader.Revenue = 0;
                            }
                        }, Material.GOLD_INGOT, 7000),

                    new Button(6, 26, "Окончить аренду", "Окончить аренду\nПрибыль и товары будут возвращены",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                trader.IsRanted = false;
                                trader.Owner    = null;
                                trader.Term     = LocalDateTime.now().toString();

                                trader.Storage.giveItemsWithoutLore(player);
                                player.giveCashToPlayer(trader.Revenue, false);
                                trader.Storage.clear();
                            }
                        }, Material.GOLD_INGOT, 7000)
                ), "Торговец-Управление", MenuSizes.ThreeLines, "\u10E2"),

                new Panel(Arrays.asList(
                    new Button(0, 21, "Арендовать", "",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader != null) {
                                var playerTradeLicense = PcConomy.GlobalLicense.getLicense(player.getUniqueId(), LicenseType.Trade);
                                if (playerTradeLicense == null) return;
                                if (!playerTradeLicense.isOverdue())
                                    getExtendedRantedWindow(player, trader);
                            }
                        }, Material.GOLD_INGOT, 7000),

                    new Button(5, 26, "НДС города:", "", null, Material.GOLD_INGOT, 7000)
                ), "Торговец-Аренда", MenuSizes.ThreeLines, "\u10E3"),

                new Panel(List.of(
                    new ClickArea(0, 8,
                        (event) -> {
                            var player = (Player) event.getWhoClicked();
                            var title = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);
                            if (trader == null) return;

                            var choseItem = event.getCurrentItem();
                            if (choseItem == null) return;

                            var days = Integer.parseInt(choseItem.getName().split(" ")[0]);
                            if (player.amountOfCashInInventory(false) < trader.Cost * days) return;
                            player.takeCashFromPlayer(trader.Cost * days, false);
                            GorodkiUniverse.getInstance().getGorod(trader.HomeTown).changeBudget(trader.Cost * days);

                            rantTrader(trader, days, player);
                            player.closeInventory();
                        })
                ), "Торговец-Аренда-Время", MenuSizes.OneLine, "\u10DF"),

                new Panel(Arrays.asList(
                    new Button(0, 20, "Установить цену", "Установить цену аренды за 1 день",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);
                            if (trader != null) getPricesWindow(player, trader);
                        }, Material.GOLD_INGOT, 7000),

                    new Button(3, 23, "Установить процент", "Установить процент с прибыли торговца",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);
                            if (trader != null) getMarginWindow(player, trader);
                        }, Material.GOLD_INGOT, 7000),

                    new Button(6, 26, "Занять", "Занять торговца бесплатно",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader == null) return;
                            rantTrader(trader, 1, player);
                            player.sendMessage("Торговец успешно занят!");
                        }, Material.GOLD_INGOT, 7000)
                ), "Торговец-Владелец", MenuSizes.ThreeLines, "\u10DC"),

                new Panel(Arrays.asList(
                    new Slider(Arrays.asList(
                        0, 1, 2, 3, 4, 5, 6, 7, 8
                    ), Arrays.asList(
                        "100" + Cash.currencySigh,  "500" + Cash.currencySigh,   "1000" + Cash.currencySigh,
                        "1500" + Cash.currencySigh, "2000" + Cash.currencySigh,  "2500" + Cash.currencySigh,
                        "5000" + Cash.currencySigh, "10000" + Cash.currencySigh, "20000" + Cash.currencySigh
                    ), "", "Цена аренды", null, 17000, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT),

                    new Button(14, 26, "Установить", "Установить выбранные цены",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);

                            if (trader == null) return;
                            var price = TraderWindow.TraderMenu.getPanel("Торговец-Цена").getSliders("Цена аренды").getChose(event);
                            if (price.equals("none")) return;

                            trader.Cost = Double.parseDouble(price.replace(Cash.currencySigh, ""));
                            player.sendMessage("Цена установлена!");
                        }, Material.GOLD_INGOT, 7000),

                    new Button(9, 21, "Отмена", "",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            player.closeInventory();
                        }, Material.GOLD_INGOT, 7000)
                ), "Торговец-Цена", MenuSizes.ThreeLines, "\u10DD"),

                new Panel(Arrays.asList(
                    new Slider(Arrays.asList(
                            0, 1, 2, 3, 4, 5, 6, 7, 8
                    ), Arrays.asList(
                            "5%", "10%", "20%", "30%", "40%", "50%", "60%", "90%", "100%"
                    ), "", "Процент города", null, 17000, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT),

                    new Button(14, 26, "Установить", "Установить процент наценки товаров",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var title  = Utils.getInventoryTitle(event);
                            var trader = getTraderFromTitle(title);
                            if (trader == null) return;

                            var percent = TraderWindow.TraderMenu.getPanel("Торговец-Процент").getSliders("Процент города").getChose(event);
                            if (percent.equals("none")) return;

                            trader.Margin = Double.parseDouble(percent.replace("%", "")) / 100;
                            player.sendMessage("Процент установлен!");
                        }, Material.GOLD_INGOT, 7000),

                    new Button(9, 21, "Отмена", "",
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            player.closeInventory();
                        }, Material.GOLD_INGOT, 7000)
                ), "Торговец-Процент", MenuSizes.ThreeLines, "\u10DD"),

                new Panel(Arrays.asList(
                    new Button(0, 21, "Купить", "",
                        (event) -> {
                            var player     = (Player)event.getWhoClicked();
                            var title      = Utils.getInventoryTitle(event);
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
                                        GorodkiUniverse.getInstance().getGorod(trader.HomeTown).changeBudget(price - endPrice);
                                        trader.Revenue += endPrice;

                                        if (TownyAPI.getInstance().getTown(player) != null)
                                            if (trader.SpecialList.contains(Objects.requireNonNull(TownyAPI.getInstance().getTown(player)).getUUID())) {
                                                player.giveCashToPlayer(price - endPrice, false);
                                                GorodkiUniverse.getInstance().getGorod(trader.HomeTown).changeBudget(-(price - endPrice));
                                                    player.sendMessage("Так как вы состоите в торговом союзе, пошлина была компенсированна городом");
                                            }
                                    }
                                }
                            }

                            getWindow(player, trader);
                        }, Material.GOLD_INGOT, 7000),

                        new Button(5, 26, "Отмена", "",
                            (event) -> {
                                var player = (Player)event.getWhoClicked();
                                var title  = Utils.getInventoryTitle(event);
                                var trader = getTraderFromTitle(title);

                                if (trader != null) getWindow(player, trader);
                            }, Material.GOLD_INGOT, 7000)
                ), "Торговец-Покупка", MenuSizes.ThreeLines, "\u10DB")
            ), "Trader", new LocalizationManager(PcConomy.Config.getString("ui.loc4trader")));

    public static void getWindow(Player player, Trader trader) {
        TraderWindow.TraderMenu.getPanel("Торговец-Ассортимент", PcConomy.Config.getString("ui.language", "RU")).resize(9 * trader.Level)
            .getViewWith(
                player,
                "Торговец-Ассортимент " + trader.getNPC().getId(),
                List.of(new ItemArea(0, Math.min(9 * trader.Level, 26), trader.Storage, null))
            );
    }

    public static void getOwnerWindow(Player player, Trader trader) {
        TraderWindow.TraderMenu.getPanel("Торговец-Управление", PcConomy.Config.getString("ui.language", "RU"))
                .getView(player, "Торговец-Управление " + trader.getNPC().getId());
    }

    public static void getRanterWindow(Player player, Trader trader) {
        TraderMenu.getPanel("Торговец-Аренда", PcConomy.Config.getString("ui.language", "RU"))
                .getView(player, "Торговец-Аренда " + trader.getNPC().getId(), Arrays.asList(
                        Arrays.asList(
                            "Окно аренды торговца. Цена за день: ",
                            trader.Cost + Cash.currencySigh
                        ),

                        List.of(
                            trader.Margin + "%"
                        )
                    ), Arrays.asList(
                        "Арендовать", "НДС города:"
                    )
                );
    }

    public static void getExtendedRantedWindow(Player player, Trader trader) {
        var components = new ArrayList<org.j1sk1ss.menuframework.objects.interactive.Component>();
        for (var i = 0; i < 9; i++)
            components.add(
                    new LittleButton(i,
                            (i + 1) + " дней",
                            trader.Cost * (i + 1) + Cash.currencySigh,
                            null, Material.GOLD_INGOT, 7002)
            );

        TraderMenu.getPanel("Торговец-Аренда-Время", PcConomy.Config.getString("ui.language", "RU")).getViewWith(
                player,
                "Торговец-Аренда-Время " + trader.getNPC().getId(),
                components);
    }

    public static void getMayorWindow(Player player, Trader trader) {
        TraderMenu.getPanel("Торговец-Владелец", PcConomy.Config.getString("ui.language", "RU"))
                .getView(player, "Торговец-Владелец " + trader.getNPC().getId());
    }

    public static void getPricesWindow(Player player, Trader trader) {
        TraderMenu.getPanel("Торговец-Цена", PcConomy.Config.getString("ui.language", "RU"))
                .getView(player, "Торговец-Цена " + trader.getNPC().getId());
    }

    public static void getMarginWindow(Player player, Trader trader) {
        TraderMenu.getPanel("Торговец-Процент", PcConomy.Config.getString("ui.language", "RU"))
                .getView(player, "Торговец-Процент " + trader.getNPC().getId());
    }

    public static void getAcceptWindow(Player player, ItemStack item, Trader trader) {
        TraderMenu.getPanel("Торговец-Покупка", PcConomy.Config.getString("ui.language", "RU")).getViewWith(
                player,
                "Торговец-Покупка " + trader.getNPC().getId(),
                List.of(new Icon(13, item.getName(), String.join("\n", item.getLoreLines()), item.getType()))
        );
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