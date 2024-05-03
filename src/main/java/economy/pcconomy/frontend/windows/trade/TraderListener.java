package economy.pcconomy.frontend.windows.trade;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.npc.traits.Trader;
import economy.pcconomy.frontend.windows.WindowListener;

import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.j1sk1ss.itemmanager.manager.Manager;

import java.time.LocalDateTime;
import java.util.Arrays;


@ExtensionMethod({Manager.class, CashManager.class})
public class TraderListener extends WindowListener {
    @SuppressWarnings("deprecation")
    @Override
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

        else if (title.contains("Торговец-Аренда-Время")) {
            var days = Integer.parseInt(choseItem.getName().split(" ")[0]);
            if (player.amountOfCashInInventory(false) < trader.Cost * days) return;
            player.takeCashFromPlayer(trader.Cost * days, false);
            PcConomy.GlobalTownManager.getTown(trader.HomeTown).changeBudget(trader.Cost * days);

            rantTrader(trader, days, player);
            player.closeInventory();
        }
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
