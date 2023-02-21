package economy.pcconomy.frontend.ui.windows.loan;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;

import economy.pcconomy.backend.bank.scripts.LoanWorker;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.scripts.ItemWorker;

import economy.pcconomy.frontend.ui.Window;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class LoanListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // Клик по кредиту
        var player = (Player) event.getWhoClicked();
        var activeInventory = event.getInventory();
        var item = event.getCurrentItem();

        if (Window.isThisWindow(event, player, "Кредит-Город")) {
            var town = TownyAPI.getInstance().getTown(player.getLocation());
            var townObject = PcConomy.GlobalTownWorker.GetTownObject(town.getName());
            var buttonPosition = event.getSlot();
            event.setCancelled(true);

            if (ItemWorker.GetName(item).contains("Выплатить кредит")) {
                var balanceWorker = new BalanceWorker();
                var loanAmount = LoanWorker.getLoan(player.getUniqueId(), townObject).amount;

                if (!balanceWorker.isSolvent(loanAmount, player)) {
                    balanceWorker.TakeMoney(loanAmount, player);
                    townObject.ChangeBudget(loanAmount);
                    LoanWorker.destroyLoan(player.getUniqueId(), townObject);

                    player.openInventory(LoanWindow.GetLoanWindow(player, false));
                }
                return;
            }

            if (ItemWorker.GetName(item).contains(CashWorker.currencySigh)) {
                boolean isSafe = ItemWorker.GetLore(item).contains("Банк одобрит данный займ.");
                final int maxCreditCount = 5;

                if (isSafe && townObject.Credit.size() < maxCreditCount) {
                    if (!townObject.Credit.contains(LoanWorker.getLoan(player.getUniqueId(), townObject))) {
                        activeInventory.setItem(buttonPosition, ItemWorker.SetMaterial(item, Material.LIGHT_BLUE_WOOL));
                        LoanWorker.createLoan(LoanWindow.GetSelectedAmount(activeInventory),
                                LoanWindow.GetSelectedDuration(activeInventory), player, townObject.Credit,
                                townObject);
                        player.closeInventory();
                    }
                }
            } else {
                activeInventory.setItem(buttonPosition, ItemWorker.SetMaterial(item, Material.PURPLE_WOOL));
                player.openInventory(LoanWindow.GetLoanWindow(activeInventory, player, buttonPosition, false));
            }
        }
    }
}
