package economy.pcconomy.frontend.ui.windows.loan;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.scripts.ItemWorker;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class LoanerListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // Клик по кредиту
        var player = (Player) event.getWhoClicked();
        var activeInventory = event.getInventory();
        var item = event.getCurrentItem();

        if (item != null) {
            if (activeInventory.getHolder() instanceof Player player1)
                if (event.getView().getTitle().equals("Кредит") && player1.equals(player)) {
                    var buttonPosition = event.getSlot();
                    event.setCancelled(true);

                    if (ItemWorker.GetName(item).contains("Выплатить кредит")) {
                        var balanceWorker = new BalanceWorker();
                        var loanAmount = PcConomy.GlobalBank.GetLoan(player.getUniqueId()).amount;

                        if (!balanceWorker.isSolvent(loanAmount, player)) {
                            balanceWorker.TakeMoney(loanAmount, player);
                            PcConomy.GlobalBank.BankBudget += loanAmount;
                            PcConomy.GlobalBank.DestroyLoan(player.getUniqueId());

                            player.openInventory(LoanWindow.GetLoanWindow(player));
                        }
                        return;
                    }

                    if (ItemWorker.GetName(item).contains(CashWorker.currencySigh)) {
                        boolean isSafe = ItemWorker.GetLore(item).contains("Банк одобрит данный займ.");

                        if (isSafe) {
                            if (!PcConomy.GlobalBank.Credit.contains(PcConomy.GlobalBank.GetLoan(player.getUniqueId()))) {
                                activeInventory.setItem(buttonPosition, ItemWorker.SetMaterial(item, Material.LIGHT_BLUE_WOOL));
                                PcConomy.GlobalBank.CreateLoan(LoanWindow.GetSelectedAmount(activeInventory),
                                        LoanWindow.GetSelectedDuration(activeInventory), player);
                                player.closeInventory();
                            }
                        }
                    } else {
                        activeInventory.setItem(buttonPosition, ItemWorker.SetMaterial(item, Material.PURPLE_WOOL));
                        player.openInventory(LoanWindow.GetLoanWindow(activeInventory, player, buttonPosition));
                    }
                }
        }
    }
}
