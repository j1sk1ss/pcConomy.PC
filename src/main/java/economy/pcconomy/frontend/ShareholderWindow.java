package economy.pcconomy.frontend;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.economy.bank.Bank;
import economy.pcconomy.backend.economy.share.objects.Share;
import economy.pcconomy.backend.economy.share.objects.ShareType;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import lombok.experimental.ExtensionMethod;
import org.j1sk1ss.itemmanager.manager.Manager;
import net.potolotcraft.gorodki.GorodkiUniverse;

import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.nonInteractive.Margin;
import org.j1sk1ss.menuframework.objects.interactive.components.*;
import org.j1sk1ss.menuframework.objects.nonInteractive.Direction;

import java.util.UUID;
import java.util.Arrays;
import java.util.Objects;
import java.util.ArrayList;


@ExtensionMethod({ Manager.class, Cash.class })
public class ShareholderWindow {
    private static final MenuWindow ShareHolderMenu = new MenuWindow(Arrays.asList(
        new Panel(Arrays.asList(
            /*
            ============================================
            Shareholder main window.
            ============================================
             */
            new Button(new Margin(0, 0, 2, 2), "Покупка/продажа акций", "Покупка и продажа акций городов на рынке",
                (event, menu) -> {
                    var player = (Player) event.getWhoClicked();
                    ShareholderWindow.sharesWindow(player, 0);
                }, Material.GOLD_INGOT, 7000),

            new Button(new Margin(0, 3, 2, 2), "Выставление акций", "Выставление акций города на рынок",
                (event, menu) -> {
                    var player = (Player) event.getWhoClicked();
                    var town = TownyAPI.getInstance().getTown(player);
                    if (town != null) {
                        if (PcConomy.getInstance().shareManager.getInteractionList().contains(town.getUUID())) {
                            player.sendMessage("Ваш город уже работал с акциями сегодня");
                            return;
                        }
                    }
                    else return;

                    if (player.equals(town.getMayor().getPlayer()))
                        ShareholderWindow.townSharesWindow(player, town.getUUID());
                }, Material.GOLD_INGOT, 7000),

            new Button(new Margin(0, 6, 2, 2), "Обналичить акции", "Будут обналичены акции в инвенторе игрока",
                (event, menu) -> {
                    var player = (Player) event.getWhoClicked();
                    var inventory = player.getInventory().getStorageContents();

                    for (var item : inventory) {
                        if (item == null) continue;
                        if (Share.isShare(item))
                            PcConomy.getInstance().shareManager.cashOutShare(player, new Share(item));
                    }
                }, Material.GOLD_INGOT, 7000)

        ), "Акции-Меню", MenuSizes.ThreeLines, "\u10D6"),

        new Panel(Arrays.asList(
            /*
            ============================================
            Shareholder list of shares.
            ============================================
             */
            new ClickArea(new Margin(0, 0, 4, 8),
                (event, menu) -> {
                    var player = (Player)event.getWhoClicked();                
                    var item = event.getCurrentItem();
                    if (item == null) return;
        
                    var townId = UUID.fromString(item.getLoreLines().get(3).split(" ")[1]);
                    var town = TownyAPI.getInstance().getTown(townId);
                    if (town == null) return;

                    ShareholderWindow.acceptWindow(player, townId);
                }),

            new Button(new Margin(5, 0, 0, 3), "Назад", "На одну страницу",
                (event, menu) -> {
                    var page = Integer.parseInt(Utils.getInventoryTitle(event).split(" ")[1]);
                    if (page >= 1) {
                        var player = (Player) event.getWhoClicked();
                        ShareholderWindow.sharesWindow(player, page - 1);
                    }
                }, Material.GOLD_INGOT, 7000),

            new Button(new Margin(5, 5, 0, 3), "Вперёд", "На одну страницу",
                (event, menu) -> {
                    var page = Integer.parseInt(Utils.getInventoryTitle(event).split(" ")[1]);
                    var player = (Player) event.getWhoClicked();
                    ShareholderWindow.sharesWindow(player, page + 1);
                }, Material.GOLD_INGOT, 7000)
        ), "Акции-Список", MenuSizes.SixLines, "\u10D7"),

        new Panel(Arrays.asList(
            /*
            ============================================
            Buy share window.
            Window, where player can buy one share.
            ============================================
             */
            new Button(new Margin(0, 5, 2, 3), "Купить одну акцию", "",
                (event, menu) -> {
                    var player = (Player) event.getWhoClicked();
                    var town   = TownyAPI.getInstance().getTown(Utils.getInventoryTitle(event).split(" ")[1]);
                    assert town != null;

                    var share  = PcConomy.getInstance().shareManager.soldFirstEmptyShare(town.getUUID());
                    if (share == null) {
                        player.sendMessage("Акции данного города не доступны к покупке (6)");
                        return;
                    }

                    if (Bank.getValueWithVat(share.getPrice()) > player.amountOfCashInInventory(false)) return;
                    share.buyShare(player);
                }, Material.GOLD_INGOT, 7000),

            new Button(new Margin(0, 0, 2, 3), "Продать одну акцию", "",
                (event, menu) -> {
                    var player = (Player) event.getWhoClicked();
                    if (Share.isShare(player.getInventory().getItemInMainHand())) {
                        var share = new Share(player.getInventory().getItemInMainHand());
                        var town = TownyAPI.getInstance().getTown(Utils.getInventoryTitle(event).split(" ")[1]);

                        if (share.getPrice() > GorodkiUniverse.getInstance().getGorod(town).getBudget()) return;
                        share.sellShare(player, player.getInventory().getItemInMainHand());
                    }
                }, Material.GOLD_INGOT, 7000)

        ), "Акции-Города", MenuSizes.ThreeLines, "\u10D8"),

        new Panel(Arrays.asList(
            /*
            ============================================
            Shareholder expose window.
            In this window, mayor can expose shares of town.
            ============================================
             */
            new Button(new Margin(0, 0, 2, 2), "Выставить на продажу", "Акции будут выставлены на продажу",
                (event, menu) -> {
                    var player = (Player) event.getWhoClicked();
                    var townSharesPanel = menu.getPanel("Акции-Выставление");
                    var town = TownyAPI.getInstance().getTown(Utils.getInventoryTitle(event).split(" ")[1]);
                    if (town == null) return;

                    var countSlider   = townSharesPanel.getComponent("Кол-во акций", Slider.class).getChose(event);
                    var percentSlider = townSharesPanel.getComponent("Процент города", Slider.class).getChose(event);
                    var costSlider    = townSharesPanel.getComponent("Цена акций", Slider.class).getChose(event);
                    var typeSlider    = townSharesPanel.getComponent("Тип акций", Slider.class).getChose(event);

                    if (costSlider.equals(Slider.SliderNone) || countSlider.equals(Slider.SliderNone) ||
                            percentSlider.equals(Slider.SliderNone) || typeSlider.equals(Slider.SliderNone)) return;
                    PcConomy.getInstance().shareManager.exposeShares(
                            town.getUUID(),
                            Double.parseDouble(costSlider.replace(Cash.currencySigh, "")),
                            Integer.parseInt(countSlider.replace("шт.", "")),
                            Double.parseDouble(percentSlider.replace("%", "")),
                            (typeSlider.equals("Дивиденты") ? ShareType.Dividends : ShareType.Equity)
                    );

                    player.sendMessage("Акции города выставлены на продажу");
                }, Material.GOLD_INGOT, 7000),

            new Button(new Margin(0, 3, 2, 2), "Снять с продажи", "Акции будут сняты с продажи",
                (event, menu) -> {
                    var player = (Player) event.getWhoClicked();
                    var town = TownyAPI.getInstance().getTown(Utils.getInventoryTitle(event).split(" ")[1]);
                    if (town == null) return;

                    PcConomy.getInstance().shareManager.takeOffShares(town.getUUID());
                    player.sendMessage("Акции города сняты с продажы");
                }, Material.GOLD_INGOT, 7000),

            new Slider(new Margin(3, 0, 8, Direction.Horizontal), Arrays.asList(
                "1шт.", "10шт.", "25шт.", "50шт.", "100шт.", "200шт.", "500шт.", "1000шт.", "10000шт."
            ), "Кол-во", "Кол-во акций", null, 17000, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT),
            new Slider(new Margin(4, 0, 8, Direction.Horizontal), Arrays.asList(
                "5%", "15%", "20%", "30%", "40%", "50%", "60%", "70%", "100%"
            ), "Процент города, который будет выставлен на биржу", "Процент города", null,
                    17000, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT),
            new Slider(new Margin(5, 0, 8, Direction.Horizontal), Arrays.asList(
                "100" + Cash.currencySigh, "500" + Cash.currencySigh, "1000" + Cash.currencySigh,
                "1500" + Cash.currencySigh, "2000" + Cash.currencySigh, "2500" + Cash.currencySigh,
                "5000" + Cash.currencySigh, "10000" + Cash.currencySigh, "20000" + Cash.currencySigh
            ), "Цена одной акции", "Цена акций", null, 17000, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT),
            new Slider(new Margin(1, 7, 1, Direction.Horizontal),
                    Arrays.asList("Дивиденты", "Доля"), "Тип", "Тип акций", null, 17001, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT)
        ), "Акции-Выставление", MenuSizes.SixLines, "\u10D9")
    ));

    public static void generateWindow(Player player) {
        ShareHolderMenu.getPanel("Акции-Меню").getView(player);
    }

    private static void sharesWindow(Player player, int windowNumber) {
        var actions = PcConomy.getInstance().shareManager.getShares().keySet().toArray();
        var list = new ArrayList<org.j1sk1ss.menuframework.objects.interactive.Component>();
        for (var i = windowNumber * 27; i < actions.length; i++)
            for (var j = i; j < i + Math.min(Math.max(actions.length - 27, 1), 27); j++) {
                var share = PcConomy.getInstance().shareManager.getTownShares((UUID) actions[j]).get(0);

                var townName = "[удалён]";
                var town = TownyAPI.getInstance().getTown((UUID) actions[j]);
                if (town != null) townName = town.getName();

                list.add(new LittleButton(new Margin(j, 0, 0),
                    "Акции города " + townName,
                    "Цена: " + share.getPrice() + Cash.currencySigh + "\n" +
                    "Доля собственности: " + share.getEquality() + "%\n" +
                    "Тип ценной бумаги: " + share.getShareType() + "\n" +
                    "ID: " + share.getTownUUID())); // TODO: DATA MODEL
            }

        ShareHolderMenu.getPanel("Акции-Список").getViewWith(player, "Акции-Список " + windowNumber, list);
    }

    private static void acceptWindow(Player player, UUID town) {
        ShareHolderMenu.getPanel("Акции-Города").getView(player, "Акции-Города " + Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getName());
    }

    private static void townSharesWindow(Player player, UUID town) {
        ShareHolderMenu.getPanel("Акции-Выставление").getView(player, "Акции-Выставление " + Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getName());
    }
}
