package economy.pcconomy.frontend.ui.windows.shareholder;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.economy.share.objects.Share;
import economy.pcconomy.backend.economy.share.objects.ShareType;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.frontend.ui.windows.Window;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.Button;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;
import org.j1sk1ss.menuframework.objects.interactive.components.Slider;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;


@ExtensionMethod({ItemManager.class})
public class ShareholderWindow extends Window {
    @SuppressWarnings("deprecation")
        public static MenuWindow ShareHolderMenu = new MenuWindow(Arrays.asList(
            new Panel(Arrays.asList(
                    new Button(0, 20, "Покупка/продажа акций", "Покупка и продажа акций городов на рынке",
                            (event) -> {
                                var player = (Player) event.getWhoClicked();
                                player.openInventory(ShareholderWindow.sharesWindow(player, 0));
                            }),

                    new Button(3, 23, "Выставление акций", "Выставление акций города на рынок",
                            (event) -> {
                                var player = (Player) event.getWhoClicked();
                                var town = TownyAPI.getInstance().getTown(player);
                                // if (town != null) TODO: Uncomment after test
                                //     if (PcConomy.GlobalShareManager.InteractionList.contains(town.getUUID())) {
                                //         player.sendMessage("Ваш город уже работал с акциями сегодня");
                                //         return;
                                //     }

                                if (player.equals(Objects.requireNonNull(town).getMayor().getPlayer()))
                                    player.openInventory(ShareholderWindow.townSharesWindow(player, town.getUUID()));
                            }),

                    new Button(6, 26, "Обналичить акции", "Будут обналичены акции в инвенторе игрока",
                            (event) -> {
                                var player = (Player) event.getWhoClicked();
                                var inventory = player.getInventory().getStorageContents();

                                for (var item : inventory) {
                                    var share = new Share(item);
                                    PcConomy.GlobalShareManager.cashOutShare(player, share);
                                }
                            })

            ), "Акции-Меню"),

            new Panel(Arrays.asList(
                new Button(0, 21, "Продать одну акцию", "",
                    (event) -> {
                        var player = (Player) event.getWhoClicked();
                        var share = new Share(player.getInventory().getItemInMainHand());
                        var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);

                        if (share.Price > PcConomy.GlobalTownManager.getTown(town.getUUID()).getBudget())
                            return;
                        share.sellShare(player, player.getInventory().getItemInMainHand());
                    }),

                new Button(5, 26, "Купить одну акцию", "",
                    (event) -> {
                        var player = (Player) event.getWhoClicked();
                        var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);
                        var shares = PcConomy.GlobalShareManager.getEmptyTownShare(town.getUUID());
                        var share = shares.get(0);

                        if (PcConomy.GlobalBank.checkVat(share.Price) > CashManager.amountOfCashInInventory(player, false))
                            return;
                        share.buyShare(player);
                    })

            ), "Акции-Города"),

            new Panel(Arrays.asList(
                new Button(0, 20, "Выставить на продажу", "Акции будут выставлены на продажу",
                    (event) -> {
                        var player = (Player) event.getWhoClicked();
                        var townSharesPanel = ShareholderWindow.ShareHolderMenu.getPanel("Акции-Выставление");
                        var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);
                        if (town == null) return;

                        var countSlider = new Slider(townSharesPanel.getSliders("SliderCount"), event.getInventory());
                        var percentSlider = new Slider(townSharesPanel.getSliders("SliderPercent"), event.getInventory());
                        var costSlider = new Slider(townSharesPanel.getSliders("SliderCost"), event.getInventory());
                        var typeSlider = new Slider(townSharesPanel.getSliders("SliderType"), event.getInventory());

                        if (costSlider.getChose(event) == null || countSlider.getChose(event) == null || percentSlider.getChose(event) == null || typeSlider.getChose(event) == null)
                            return;
                        PcConomy.GlobalShareManager.exposeShares(
                                town.getUUID(),
                                Double.parseDouble(costSlider.getChose(event).replace(CashManager.currencySigh, "")),
                                Integer.parseInt(countSlider.getChose(event).replace("шт.", "")),
                                Double.parseDouble(percentSlider.getChose(event).replace("%", "")),
                                (typeSlider.getName().equals("Дивиденты") ? ShareType.Dividends : ShareType.Equity)
                        );

                        player.sendMessage("Акции города выставлены на продажу");
                    }),

                new Button(3, 23, "Снять с продажи", "Акции будут сняты с продажи",
                    (event) -> {
                        var player = (Player) event.getWhoClicked();
                        var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);
                        if (town == null) return;

                        PcConomy.GlobalShareManager.takeOffShares(town.getUUID());
                        player.sendMessage("Акции города сняты с продажы");
                    }),

                new Slider(Arrays.asList(
                        27, 28, 29, 30, 31, 32, 33, 34, 35
                ), Arrays.asList(
                        "1шт.", "10шт.", "25шт.", "50шт.", "100шт.", "200шт.", "500шт.", "1000шт.", "10000шт."
                ), "Кол-во", "SliderCount", null),
                new Slider(Arrays.asList(
                        36, 37, 38, 39, 40, 41, 42, 43, 44
                ), Arrays.asList(
                        "5%", "15%", "20%", "30%", "40%", "50%", "60%", "70%", "100%"
                ), "Процент", "SliderPercent", null),
                new Slider(Arrays.asList(
                        45, 46, 47, 48, 49, 50, 51, 52, 53
                ), Arrays.asList(
                        "100" + CashManager.currencySigh, "500" + CashManager.currencySigh, "1000" + CashManager.currencySigh,
                        "1500" + CashManager.currencySigh, "2000" + CashManager.currencySigh, "2500" + CashManager.currencySigh,
                        "5000" + CashManager.currencySigh, "10000" + CashManager.currencySigh, "20000" + CashManager.currencySigh
                ), "Цена", "SliderCost", null),
                new Slider(Arrays.asList(
                        16, 17
                ), Arrays.asList("Дивиденты", "Доля"), "Тип", "SliderType", null)
            ), "Акции-Выставление")
    )) {
    };

    @Override
    public Inventory generateWindow(Player player) {
        var window = Bukkit.createInventory(player, 27, Component.text("Акции-Меню"));
        ShareHolderMenu.getPanel("Акции-Меню").place(window);

        return window;
    }

    public static Inventory sharesWindow(Player player, int windowNumber) {
        var window = Bukkit.createInventory(player, 27, Component.text("Акции-Список"));
        var actions = PcConomy.GlobalShareManager.Shares.keySet().toArray();

        for (var i = windowNumber * 27; i < actions.length; i++)
            for (var j = i; j < i + Math.min(Math.max(actions.length - 27, 1), 27); j++) {
                var share = PcConomy.GlobalShareManager.getTownShares((UUID) actions[j]).get(0);
                window.setItem(j, new Item(
                        "Акции города " + Objects.requireNonNull(TownyAPI.getInstance().getTown((UUID) actions[j])).getName(),
                        "Цена: " + share.Price + CashManager.currencySigh + "\n" +
                        "Доля собственности: " + share.Equality + "%\n" +
                        "Тип ценной бумаги: " + share.ShareType + "\n" +
                        "ID: " + share.TownUUID,
                        Material.PAPER, 1, 17000)); // TODO: DATA MODEL
            }

        return window;
    }

    public static Inventory acceptWindow(Player player, UUID town) {
        var window = Bukkit.createInventory(player, 27, Component.text("Акции-Города " + Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getName()));
        ShareHolderMenu.getPanel("Акции-Города").place(window);

        return window;
    }

    public static Inventory townSharesWindow(Player player, UUID town) {
        var window = Bukkit.createInventory(player, 54, Component.text("Акции-Выставление " + Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getName()));
        ShareHolderMenu.getPanel("Акции-Выставление").place(window);

        return window;
    }
}
