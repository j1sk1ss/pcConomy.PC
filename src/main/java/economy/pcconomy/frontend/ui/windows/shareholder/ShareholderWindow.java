package economy.pcconomy.frontend.ui.windows.shareholder;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.frontend.ui.objects.Menu;
import economy.pcconomy.frontend.ui.objects.Panel;
import economy.pcconomy.frontend.ui.objects.interactive.Button;
import economy.pcconomy.frontend.ui.objects.interactive.Slider;

import economy.pcconomy.frontend.ui.windows.Window;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ShareholderWindow extends Window {
    public static Menu ShareHolderMenu = new Menu(Arrays.asList(
            new Panel(Arrays.asList(
                    new Button(0, 21, "Покупка/продажа акций", ""),
                    new Button(5, 26, "Выставление акций", "")
            ), "Акции-Меню"),
            new Panel(Arrays.asList(
                    new Button(0 ,21, "Продать одну акцию", ""),
                    new Button(5, 26, "Купить одну акцию", "")
            ), "Акции-Города"),
            new Panel(Arrays.asList(
                    new Button(0, 20, "Выставить на продажу", ""),
                    new Button(3, 23, "Снять с продажи", ""),
                    new Slider(Arrays.asList(
                            27, 28, 29, 30, 31, 32, 33, 34, 35
                    ), Arrays.asList(
                            "1шт.", "10шт.", "25шт.", "50шт.", "100шт.", "200шт.", "500шт.", "1000шт.", "10000шт."
                    ), "Кол-во", "SliderCount"),
                    new Slider(Arrays.asList(
                            36, 37, 38, 39, 40, 41, 42, 43, 44
                    ), Arrays.asList(
                            "5%", "15%", "20%", "30%", "40%", "50%", "60%", "70%", "100%"
                    ), "Процент", "SliderPercent"),
                    new Slider(Arrays.asList(
                            45, 46, 47, 48, 49, 50, 51, 52, 53
                    ), Arrays.asList(
                            "100" + CashManager.currencySigh, "500" + CashManager.currencySigh, "1000" + CashManager.currencySigh,
                            "1500" + CashManager.currencySigh, "2000" + CashManager.currencySigh, "2500" + CashManager.currencySigh,
                            "5000" + CashManager.currencySigh, "10000" + CashManager.currencySigh, "20000" + CashManager.currencySigh
                    ), "Цена", "SliderCost"),
                    new Slider(Arrays.asList(
                            16, 17
                    ), Arrays.asList("Дивиденты", "Доля"), "Тип", "SliderType")
            ), "Акции-Выставление")
    ));

    @Override
    public Inventory generateWindow(Player player) {
        return ShareHolderMenu.getPanel("Акции-Меню").placeComponents(Bukkit.createInventory(player, 27, Component.text("Акции-Меню")));
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
        return ShareHolderMenu.getPanel("Акции-Города").placeComponents(Bukkit.createInventory(player, 27,
                Component.text("Акции-Города " + Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getName())));
    }

    public static Inventory townSharesWindow(Player player, UUID town) {
        return ShareHolderMenu.getPanel("Акции-Выставление").placeComponents(Bukkit.createInventory(player, 54, Component.text("Акции-Выставление "
        + Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getName())));
    }
}
