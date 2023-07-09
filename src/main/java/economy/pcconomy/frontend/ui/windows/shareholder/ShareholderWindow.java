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

    public Inventory sharesWindow(Player player, int windowNumber) {
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

    public Inventory acceptWindow(Player player) {
        return acceptPanel.placeComponents(Bukkit.createInventory(player, 9, Component.text("Акции-Взаимодействие")));
    }

    public static Panel townSharesPanel = new Panel(Arrays.asList(
            new Button(Arrays.asList(

            ), "Выставить на продажу", ""),
            new Button(Arrays.asList(

            ), "Снять с продажи", ""),
            new Slider(Arrays.asList(

            ), Arrays.asList(

            ), 17000, 17000),
            new Slider(Arrays.asList(

            ), Arrays.asList(

            ), 17000, 17000),
            new Slider(Arrays.asList(

            ), Arrays.asList(

            ), 17000, 17000)
    ));

    public Inventory townSharesWindow(Player player, UUID town) {
        return townSharesPanel.placeComponents(Bukkit.createInventory(player, 54, Component.text("Акции-Выставление")));
    }
}
