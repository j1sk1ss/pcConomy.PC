package economy.pcconomy.frontend.ui.windows.trade;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.backend.npc.traits.Trader;
import economy.pcconomy.frontend.ui.windows.IWindowListener;

import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.time.LocalDateTime;
import java.util.Arrays;


@ExtensionMethod({ItemManager.class})
public class TraderListener implements IWindowListener {
    @SuppressWarnings("deprecation")
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var title  = event.getView().getTitle();
        var trader = getTraderFromTitle(title);
        if (trader == null) return;

        var choseItem = event.getCurrentItem();
        if (choseItem == null) return;


        if (title.contains("Торговец-Ассортимент")) {
            if (!player.getInventory().contains(choseItem)) player.openInventory(TraderWindow.getAcceptWindow(player, choseItem, trader));
        }
        else if (title.contains("Торговец-Управление")) {
            TraderWindow.TraderMenu.getPanel("Торговец-Управление").click(event);
        }

        else if (title.contains("Торговец-Аренда-Время")) {
            var days = Integer.parseInt(choseItem.getName().split(" ")[0]);
            if (CashManager.amountOfCashInInventory(player, false) < trader.Cost * days) return;
            CashManager.takeCashFromPlayer(trader.Cost * days, player, false);
            PcConomy.GlobalTownManager.getTown(trader.HomeTown).changeBudget(trader.Cost * days);

            rantTrader(trader, days, player);
            player.closeInventory();
        }

        else if (title.contains("Торговец-Аренда")) TraderWindow.TraderMenu.getPanel("Торговец-Аренда").click(event);
        else if (title.contains("Торговец-Владелец")) TraderWindow.TraderMenu.getPanel("Торговец-Владелец").click(event);
        else if (title.contains("Торговец-Цена")) TraderWindow.TraderMenu.getPanel("Торговец-Цена").click(event);
        else if (title.contains("Торговец-Процент")) TraderWindow.TraderMenu.getPanel("Торговец-Процент").click(event);
        else if (title.contains("Торговец-Покупка")) TraderWindow.TraderMenu.getPanel("Торговец-Покупка").click(event);
    }

    public static Trader getTraderFromTitle(String name) {
        try {
            if (Arrays.stream(name.split(" ")).toList().size() <= 1) return null;
            return PcConomy.GlobalNPC.getNPC(Integer.parseInt(name.split(" ")[1])).getOrAddTrait(Trader.class);
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
