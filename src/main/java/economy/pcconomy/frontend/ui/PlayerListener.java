package economy.pcconomy.frontend.ui;

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

public class PlayerListener implements Listener {
    @EventHandler
    public void EventHandled(InventoryClickEvent event) {
        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player currentPlayer) {
                String windowTitle = event.getView().getTitle();
                if (!currentPlayer.equals((Player) event.getWhoClicked())) return;

                if (windowTitle.contains("Кредит-Город"))
                    LoanListener.onClick(event);
                else if (windowTitle.contains("Меню"))
                    MayorListener.onClick(event);
                else if (windowTitle.contains("Банк"))
                    BankerListener.onClick(event);
                else if (windowTitle.contains("Торговец"))
                    TraderListener.onClick(event);
                else if (windowTitle.contains("Лицензии"))
                    LicensorListener.onClick(event);
                else if (windowTitle.contains("Магазин"))
                    NPCTraderListener.onClick(event);
                else if (windowTitle.contains("Кредит-Банк"))
                    NPCLoanerListener.onClick(event);
                else if (windowTitle.contains("Акции-Меню") ||
                         windowTitle.contains("Акции-Список") ||
                         windowTitle.contains("Акции-Города") ||
                         windowTitle.contains("Акции-Выставление"))
                    ShareholderListener.onClick(event);
                else if (windowTitle.contains("Кошелёк"))
                    WalletListener.onClick(event);
                else return;

                event.setCancelled(true);
            }
    }
}
