package economy.pcconomy.frontend;

import economy.pcconomy.frontend.windows.WindowListener;
import economy.pcconomy.frontend.windows.bank.BankerListener;
import economy.pcconomy.frontend.windows.loans.loan.LoanListener;
import economy.pcconomy.frontend.windows.mayor.MayorManagerListener;
import economy.pcconomy.frontend.windows.npcTrade.NPCTraderListener;
import economy.pcconomy.frontend.windows.shareholder.ShareholderListener;
import economy.pcconomy.frontend.windows.trade.TraderListener;

import economy.pcconomy.frontend.windows.wallet.WalletListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;


public class PlayerListener implements Listener {
    public PlayerListener() {
        windows = new HashMap<>();
        windows.put("Кредит-Город", new LoanListener());
        windows.put("Мир-Банк", new BankerListener());
        windows.put("Торговец", new TraderListener());
        windows.put("Магазин", new NPCTraderListener());
        windows.put("Кошелёк", new WalletListener());
        windows.put("Город-Торговцы", new MayorManagerListener());

        var share = new ShareholderListener();
        windows.put("Акции-Меню", share);
        windows.put("Акции-Список", share);
        windows.put("Акции-Города", share);
        windows.put("Акции-Выставление", share);
    }

    private final Map<String, WindowListener> windows;

    @SuppressWarnings("deprecation")
    @EventHandler
    public void EventHandled(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getInventory().getHolder() instanceof Player currentPlayer) {
            String windowTitle = event.getView().getTitle();
            if (!currentPlayer.equals(event.getWhoClicked())) return;
            for (var key : windows.keySet()) {
                if (windowTitle.contains(key)) {
                    windows.get(key).onClick(event);
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void InteractHandler(PlayerInteractEvent event) {
        for (var key : windows.keySet())
            windows.get(key).onInteract(event);
    }
}
