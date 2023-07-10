package economy.pcconomy.frontend.ui.windows.shareholder;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.frontend.ui.objects.Panel;
import economy.pcconomy.frontend.ui.objects.interactive.Button;
import economy.pcconomy.frontend.ui.objects.interactive.Slider;
import economy.pcconomy.frontend.ui.windows.IWindow;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.UUID;

public class ShareholderWindow implements IWindow {
    public static Panel actionsMenuPanel = new Panel(Arrays.asList(
            new Button(Arrays.asList(

            ), "Покупка/продажа акций", ""),
            new Button(Arrays.asList(

            ), "Выставление акций", "")
    ));

    @Override
    public Inventory generateWindow(Player player) {
        return actionsMenuPanel.placeComponents(Bukkit.createInventory(player, 27, Component.text("Акции-Меню")));
    }

    public static Inventory sharesWindow(Player player, int windowNumber) {
        var window = Bukkit.createInventory(player, 27, Component.text("Акции-Список"));
        var actions = PcConomy.GlobalShareManager.Shares.keySet().toArray();

        for (var i = windowNumber * 27; i < actions.length; i++)
            for (var j = i; j < i + Math.min(actions.length - 27, 27); j++) {
                var share = PcConomy.GlobalShareManager.getTownShares((UUID) actions[j]).get(0);
                window.setItem(j, new Item(
                        "Акция города " + TownyAPI.getInstance().getTown((UUID) actions[j]).getName(),
                        "Цена: " + share.Price + CashManager.currencySigh + "\n" +
                        "Доля собственности: " + share.Equality + "%\n" +
                        "Тип ценной бумаги: " + share.ShareType + "\n" +
                        "ID: " + share.TownUUID,
                        Material.PAPER, 17000)); // TODO: DATA MODEL
            }

        return window;
    }

    public static Panel acceptPanel = new Panel(Arrays.asList(
            new Button(Arrays.asList(

            ), "Продать одну акцию", ""),
            new Button(Arrays.asList(

            ), "Купить одну акцию", "")
    ));

    public static Inventory acceptWindow(Player player, UUID town) {
        return acceptPanel.placeComponents(Bukkit.createInventory(player, 9,
                Component.text("Акции-Города " + TownyAPI.getInstance().getTown(town).getName())));
    }

    public static Panel townSharesPanel = new Panel(Arrays.asList(
            new Button(Arrays.asList(

            ), "Выставить на продажу", ""),
            new Button(Arrays.asList(

            ), "Снять с продажи", ""),
            new Slider(Arrays.asList(

            ), Arrays.asList( //TODO: DATA MODEL
                    new Item("1 шт.", "Кол-во", Material.GLASS, 1, 17000),
                    new Item("10 шт.", "Кол-во", Material.GLASS, 1, 17000),
                    new Item("50 шт.", "Кол-во", Material.GLASS, 1, 17000),
                    new Item("100 шт.", "Кол-во", Material.GLASS, 1, 17000),
                    new Item("150 шт.", "Кол-во", Material.GLASS, 1, 17000),
                    new Item("200 шт.", "Кол-во", Material.GLASS, 1, 17000),
                    new Item("250 шт.", "Кол-во", Material.GLASS, 1, 17000),
                    new Item("500 шт.", "Кол-во", Material.GLASS, 1, 17000),
                    new Item("1000 шт.", "Кол-во", Material.GLASS, 1, 17000)
            ), 17000, 17000, "SliderCount"),
            new Slider(Arrays.asList(

            ), Arrays.asList( //TODO: DATA MODEL
                    new Item("10%", "Процент", Material.GLASS, 1, 17000),
                    new Item("15%", "Процент", Material.GLASS, 1, 17000),
                    new Item("20%", "Процент", Material.GLASS, 1, 17000),
                    new Item("25%", "Процент", Material.GLASS, 1, 17000),
                    new Item("30%", "Процент", Material.GLASS, 1, 17000),
                    new Item("35%", "Процент", Material.GLASS, 1, 17000),
                    new Item("50%", "Процент", Material.GLASS, 1, 17000),
                    new Item("65%", "Процент", Material.GLASS, 1, 17000),
                    new Item("75%", "Процент", Material.GLASS, 1, 17000)
            ), 17000, 17000, "SliderPercent"),
            new Slider(Arrays.asList(

            ), Arrays.asList( //TODO: DATA MODEL
                    new Item("100" + CashManager.currencySigh, "Цена", Material.GLASS, 1, 17000),
                    new Item("400" + CashManager.currencySigh, "Цена", Material.GLASS, 1, 17000),
                    new Item("800" + CashManager.currencySigh, "Цена", Material.GLASS, 1, 17000),
                    new Item("1600" + CashManager.currencySigh, "Цена", Material.GLASS, 1, 17000),
                    new Item("2000" + CashManager.currencySigh, "Цена", Material.GLASS, 1, 17000),
                    new Item("5000" + CashManager.currencySigh, "Цена", Material.GLASS, 1, 17000),
                    new Item("6000" + CashManager.currencySigh, "Цена", Material.GLASS, 1, 17000),
                    new Item("8000" + CashManager.currencySigh, "Цена", Material.GLASS, 1, 17000),
                    new Item("10000" + CashManager.currencySigh, "Цена", Material.GLASS, 1, 17000)
            ), 17000, 17000, "SliderCost"),
            new Slider(Arrays.asList(

            ), Arrays.asList( //TODO: DATA MODEL
                    new Item("Дивиденты", "Тип", Material.GLASS, 1, 17000),
                    new Item("Доля", "Тип", Material.GLASS, 1, 17000)
            ), 17000, 17000, "SliderType")
    ));

    public static Inventory townSharesWindow(Player player, UUID town) {
        return townSharesPanel.placeComponents(Bukkit.createInventory(player, 54, Component.text("Акции-Выставление "
        + TownyAPI.getInstance().getTown(town).getName())));
    }
}
