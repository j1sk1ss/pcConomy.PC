package economy.pcconomy.frontend.ui.windows.mayor.manager;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.npc.traits.Trader;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.frontend.ui.windows.Window;
import net.citizensnpcs.api.CitizensAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.j1sk1ss.menuframework.objects.interactive.components.Button;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;

import java.util.Arrays;

import static economy.pcconomy.frontend.ui.windows.trade.TraderListener.getTraderFromTitle;


public class MayorManagerWindow extends Window {
    @SuppressWarnings("deprecation")
    public static final org.j1sk1ss.menuframework.objects.interactive.components.Panel TraderManager = new Panel(Arrays.asList(
        new Button(0, 20, "Уволить торговца", "Торговец будет уволен",
            (event) -> {
                var title  = event.getView().getTitle();
                var trader = getTraderFromTitle(title);
                if (trader != null) trader.destroy();
            }),

        new Button(3, 23, "Переместить торговца", "Торговец будет перемещён в место вашего клика",
            (event) -> {
                // var player = (Player)event.getWhoClicked();
                var title  = event.getView().getTitle();
                var trader = getTraderFromTitle(title);
                if (trader == null) return;
            }),

        new Button(6, 26, "Улучшить торговца", "Торговец будет улучшен (+9 слотов)",
            (event) -> {
                // var player = (Player)event.getWhoClicked();
                var title  = event.getView().getTitle();
                var trader = getTraderFromTitle(title);
                if (trader != null) trader.Level = Math.max(trader.Level + 1, 3);
            })
    ), "Город-Торговцы-Управление");


    @Override
    public Inventory generateWindow(Player player) {
        var window = Bukkit.createInventory(player, 27, Component.text("Город-Торговцы"));
        var town   = PcConomy.GlobalTownManager.getTown(TownyAPI.getInstance().getTown(player).getUUID());
        for (var i = 0; i < Math.min(27, town.traders.size()); i++) {
            var trader = CitizensAPI.getNPCRegistry().getById(town.traders.get(i)).getOrAddTrait(Trader.class);
            window.setItem(i, new Item(town.traders.get(i) + "",
                    "Ranted: " + trader.IsRanted + "\nMargin: " + trader.Margin + "\nRant price: " + trader.Cost));
        }

        return window;
    }

    public static Inventory generateTradeControls(Player player, int traderId) {
        var window = Bukkit.createInventory(player, 27, Component.text("Город-Торговцы-Управление " + traderId));
        TraderManager.place(window);
        return window;
    }
}
