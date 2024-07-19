package economy.pcconomy.frontend;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.economy.bank.Bank;
import economy.pcconomy.backend.economy.share.objects.Share;
import economy.pcconomy.backend.economy.share.objects.ShareType;
import lombok.experimental.ExtensionMethod;

import net.potolotcraft.gorodki.GorodkiUniverse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.j1sk1ss.itemmanager.manager.Manager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;


@ExtensionMethod({ Manager.class, Cash.class })
public class ShareholderWindow {
    public static MenuWindow ShareHolderMenu = new MenuWindow(Arrays.asList(
        new Panel(Arrays.asList(
            new Button(0, 20, "Покупка/продажа акций", "Покупка и продажа акций городов на рынке",
                (event) -> {
                    var player = (Player) event.getWhoClicked();
                    ShareholderWindow.sharesWindow(player, 0);
                }, Material.GOLD_INGOT, 7000),

            new Button(3, 23, "Выставление акций", "Выставление акций города на рынок",
                (event) -> {
                    var player = (Player) event.getWhoClicked();
                    var town = TownyAPI.getInstance().getTown(player);
                    if (town != null) {
                        if (PcConomy.GlobalShare.InteractionList.contains(town.getUUID())) {
                            player.sendMessage("Ваш город уже работал с акциями сегодня");
                            return;
                        }
                    }
                    else return;

                    if (player.equals(town.getMayor().getPlayer()))
                        ShareholderWindow.townSharesWindow(player, town.getUUID());
                }, Material.GOLD_INGOT, 7000),

            new Button(6, 26, "Обналичить акции", "Будут обналичены акции в инвенторе игрока",
                (event) -> {
                    var player = (Player) event.getWhoClicked();
                    var inventory = player.getInventory().getStorageContents();

                    for (var item : inventory) {
                        if (Share.isShare(item))
                            PcConomy.GlobalShare.cashOutShare(player, new Share(item));
                    }
                }, Material.GOLD_INGOT, 7000)

        ), "Акции-Меню", MenuSizes.ThreeLines, "\u10D6"),

        new Panel(Arrays.asList(
            new ClickArea(0, 44, 
                (event) -> {
                    var player = (Player)event.getWhoClicked();                
                    var item = event.getCurrentItem();
                    if (item == null) return;
        
                    var townId = UUID.fromString(item.getLoreLines().get(3).split(" ")[1]);
                    var town = TownyAPI.getInstance().getTown(townId);
                    if (town == null) return;

                    ShareholderWindow.acceptWindow(player, townId);
                }),

            new Button(45, 48, "Назад", "На одну страницу", 
                (event) -> {
                    var page = Integer.parseInt(Utils.getInventoryTitle(event).split(" ")[1]);
                    if (page >= 1) {
                        var player = (Player) event.getWhoClicked();
                        ShareholderWindow.sharesWindow(player, page - 1);
                    }
                }, Material.GOLD_INGOT, 7000),

            new Button(50, 53, "Вперёд", "На одну страницу", 
                (event) -> {
                    var page = Integer.parseInt(Utils.getInventoryTitle(event).split(" ")[1]);
                    var player = (Player) event.getWhoClicked();
                    ShareholderWindow.sharesWindow(player, page + 1);
                }, Material.GOLD_INGOT, 7000)
        ), "Акции-Список", MenuSizes.SixLines, "\u10D7"),

        new Panel(Arrays.asList(
            new Button(0, 21, "Купить одну акцию", "",
                (event) -> {
                    var player = (Player) event.getWhoClicked();
                    var town   = TownyAPI.getInstance().getTown(Utils.getInventoryTitle(event).split(" ")[1]);
                    assert town != null;

                    var share  = PcConomy.GlobalShare.soldFirstEmptyShare(town.getUUID());
                    if (share == null) {
                        player.sendMessage("Акции данного города не доступны к покупке (6)");
                        return;
                    }

                    if (Bank.getValueWithVat(share.getPrice()) > player.amountOfCashInInventory(false)) return;
                    share.buyShare(player);
                }, Material.GOLD_INGOT, 7000),

            new Button(5, 26, "Продать одну акцию", "",
                (event) -> {
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
            new Button(0, 20, "Выставить на продажу", "Акции будут выставлены на продажу",
                (event) -> {
                    var player = (Player) event.getWhoClicked();
                    var townSharesPanel = ShareholderWindow.ShareHolderMenu.getPanel("Акции-Выставление");
                    var town = TownyAPI.getInstance().getTown(Utils.getInventoryTitle(event).split(" ")[1]);
                    if (town == null) return;

                    var countSlider   = townSharesPanel.getSliders("Кол-во акций").getChose(event);
                    var percentSlider = townSharesPanel.getSliders("Процент города").getChose(event);
                    var costSlider    = townSharesPanel.getSliders("Цена акций").getChose(event);
                    var typeSlider    = townSharesPanel.getSliders("Тип акций").getChose(event);

                    if (costSlider.equals("none") || countSlider.equals("none") || percentSlider.equals("none") || typeSlider.equals("none")) return;
                    PcConomy.GlobalShare.exposeShares(
                            town.getUUID(),
                            Double.parseDouble(costSlider.replace(Cash.currencySigh, "")),
                            Integer.parseInt(countSlider.replace("шт.", "")),
                            Double.parseDouble(percentSlider.replace("%", "")),
                            (typeSlider.equals("Дивиденты") ? ShareType.Dividends : ShareType.Equity)
                    );

                    player.sendMessage("Акции города выставлены на продажу");
                }, Material.GOLD_INGOT, 7000),

            new Button(3, 23, "Снять с продажи", "Акции будут сняты с продажи",
                (event) -> {
                    var player = (Player) event.getWhoClicked();
                    var town = TownyAPI.getInstance().getTown(Utils.getInventoryTitle(event).split(" ")[1]);
                    if (town == null) return;

                    PcConomy.GlobalShare.takeOffShares(town.getUUID());
                    player.sendMessage("Акции города сняты с продажы");
                }, Material.GOLD_INGOT, 7000),

            new Slider(Arrays.asList(
                27, 28, 29, 30, 31, 32, 33, 34, 35
            ), Arrays.asList(
                "1шт.", "10шт.", "25шт.", "50шт.", "100шт.", "200шт.", "500шт.", "1000шт.", "10000шт."
            ), "Кол-во", "Кол-во акций", null, 17000, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT),
            new Slider(Arrays.asList(
                36, 37, 38, 39, 40, 41, 42, 43, 44
            ), Arrays.asList(
                "5%", "15%", "20%", "30%", "40%", "50%", "60%", "70%", "100%"
            ), "Процент города, который будет выставлен на биржу", "Процент города", null,
                    17000, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT),
            new Slider(Arrays.asList(
                45, 46, 47, 48, 49, 50, 51, 52, 53
            ), Arrays.asList(
                "100" + Cash.currencySigh, "500" + Cash.currencySigh, "1000" + Cash.currencySigh,
                "1500" + Cash.currencySigh, "2000" + Cash.currencySigh, "2500" + Cash.currencySigh,
                "5000" + Cash.currencySigh, "10000" + Cash.currencySigh, "20000" + Cash.currencySigh
            ), "Цена одной акции", "Цена акций", null, 17000, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT),
            new Slider(Arrays.asList(
                16, 17
            ), Arrays.asList("Дивиденты", "Доля"), "Тип", "Тип акций", null, 17001, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT)
        ), "Акции-Выставление", MenuSizes.SixLines, "\u10D9")
    ));

    public static void generateWindow(Player player) {
        ShareHolderMenu.getPanel("Акции-Меню").getView(player);
    }

    public static void sharesWindow(Player player, int windowNumber) {
        var actions = PcConomy.GlobalShare.Shares.keySet().toArray();
        var list = new ArrayList<org.j1sk1ss.menuframework.objects.interactive.Component>();
        for (var i = windowNumber * 27; i < actions.length; i++)
            for (var j = i; j < i + Math.min(Math.max(actions.length - 27, 1), 27); j++) {
                var share = PcConomy.GlobalShare.getTownShares((UUID) actions[j]).get(0);

                var townName = "[удалён]";
                var town = TownyAPI.getInstance().getTown((UUID) actions[j]);
                if (town != null) townName = town.getName();

                list.add(new LittleButton(j,
                    "Акции города " + townName,
                    "Цена: " + share.getPrice() + Cash.currencySigh + "\n" +
                    "Доля собственности: " + share.getEquality() + "%\n" +
                    "Тип ценной бумаги: " + share.getShareType() + "\n" +
                    "ID: " + share.getTownUUID())); // TODO: DATA MODEL
            }

        ShareHolderMenu.getPanel("Акции-Список").getViewWith(player, "Акции-Список " + windowNumber, list);
    }

    public static void acceptWindow(Player player, UUID town) {
        ShareHolderMenu.getPanel("Акции-Города").getView(player, "Акции-Города " + Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getName());
    }

    public static void townSharesWindow(Player player, UUID town) {
        ShareHolderMenu.getPanel("Акции-Выставление").getView(player, "Акции-Выставление " + Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getName());
    }
}
