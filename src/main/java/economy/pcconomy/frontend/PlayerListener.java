package economy.pcconomy.frontend;

import economy.pcconomy.frontend.windows.IWindowListener;
import economy.pcconomy.frontend.windows.bank.BankerListener;
import economy.pcconomy.frontend.windows.license.LicensorListener;
import economy.pcconomy.frontend.windows.loans.loan.LoanListener;
import economy.pcconomy.frontend.windows.mayor.manager.MayorManagerListener;
import economy.pcconomy.frontend.windows.npcTrade.NPCTraderListener;
import economy.pcconomy.frontend.windows.shareholder.ShareholderListener;
import economy.pcconomy.frontend.windows.trade.TraderListener;

import economy.pcconomy.frontend.windows.wallet.WalletListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;


public class PlayerListener implements Listener {
    public PlayerListener() {
        windows = new HashMap<>();
        windows.put("Кредит-Город", new LoanListener());
        windows.put("Мир-Банк", new BankerListener());
        windows.put("Торговец", new TraderListener());
        windows.put("Мир-Лицензии", new LicensorListener());
        windows.put("Магазин", new NPCTraderListener());
        windows.put("Кошелёк", new WalletListener());
        windows.put("Город-Торговцы", new MayorManagerListener());

        var share = new ShareholderListener();
        windows.put("Акции-Меню", share);
        windows.put("Акции-Список", share);
        windows.put("Акции-Города", share);
        windows.put("Акции-Выставление", share);
    }


    private final Map<String, IWindowListener> windows;


    @SuppressWarnings("deprecation")
    @EventHandler
    public void EventHandled(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;

        if (event.getInventory().getHolder() instanceof Player currentPlayer) {
            String windowTitle = event.getView().getTitle();
            if (!currentPlayer.equals(event.getWhoClicked())) return;
            for (var key : windows.keySet()) {
                if (windowTitle.contains(key)) {
                    var listener = windows.get(key);
                    listener.onClick(event);
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
}
