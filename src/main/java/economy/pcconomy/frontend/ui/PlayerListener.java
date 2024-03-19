package economy.pcconomy.frontend.ui;

import economy.pcconomy.frontend.ui.windows.IWindowListener;
import economy.pcconomy.frontend.ui.windows.bank.BankerListener;
import economy.pcconomy.frontend.ui.windows.license.LicensorListener;
import economy.pcconomy.frontend.ui.windows.loans.loan.LoanListener;
import economy.pcconomy.frontend.ui.windows.loans.npcLoan.NPCLoanerListener;
import economy.pcconomy.frontend.ui.windows.mayor.MayorListener;
import economy.pcconomy.frontend.ui.windows.npcTrade.NPCTraderListener;
import economy.pcconomy.frontend.ui.windows.shareholder.ShareholderListener;
import economy.pcconomy.frontend.ui.windows.trade.TraderListener;

import economy.pcconomy.frontend.ui.windows.wallet.WalletListener;
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
        windows.put("Меню", new MayorListener());
        windows.put("Банк", new BankerListener());
        windows.put("Торговец", new TraderListener());
        windows.put("Лицензии", new LicensorListener());
        windows.put("Магазин", new NPCTraderListener());
        windows.put("Кредит-Банк", new NPCLoanerListener());
        windows.put("Кошелёк", new WalletListener());

        var share = new ShareholderListener();
        windows.put("Акции-Меню", share);
        windows.put("Акции-Список", share);
        windows.put("Акции-Города", share);
        windows.put("Акции-Выставление", share);
    }

    private final Map<String, IWindowListener> windows;

    @EventHandler
    public void EventHandled(InventoryClickEvent event) {
        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player currentPlayer) {
                String windowTitle = event.getView().getTitle();
                if (!currentPlayer.equals(event.getWhoClicked())) return;

                for (var key : windows.keySet()) {
                    if (windowTitle.contains(key)) {
                        var listener = windows.get(key);
                        if (listener == null) return;

                        listener.onClick(event);
                        event.setCancelled(true);
                        break;
                    }
                }
            }
    }
}
