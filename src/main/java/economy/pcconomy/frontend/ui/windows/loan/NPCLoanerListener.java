package economy.pcconomy.frontend.ui.windows.loan;

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

public class NPCLoanerListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // Клик по кредиту
        var player = (Player) event.getWhoClicked();
        var activeInventory = event.getInventory();
        var item = event.getCurrentItem();

        if (Window.isThisWindow(event, player, "Кредит")) {
            var buttonPosition = event.getSlot();
            event.setCancelled(true);

            if (ItemWorker.GetName(item).contains("Выплатить кредит")) {
                LoanWorker.payOffADebt(player, PcConomy.GlobalBank);
                player.closeInventory();
            }

            if (ItemWorker.GetName(item).contains(CashWorker.currencySigh)) {
                boolean isSafe = ItemWorker.GetLore(item).contains("Банк одобрит данный займ.");

                if (isSafe) {
                    if (!PcConomy.GlobalBank.Credit.contains(LoanWorker.getLoan(player.getUniqueId(), PcConomy.GlobalBank))) {
                        activeInventory.setItem(buttonPosition, ItemWorker.SetMaterial(item, Material.LIGHT_BLUE_WOOL));
                        LoanWorker.createLoan(LoanWindow.GetSelectedAmount(activeInventory),
                                LoanWindow.GetSelectedDuration(activeInventory), player, PcConomy.GlobalBank.Credit,
                                PcConomy.GlobalBank);
                        player.closeInventory();
                    }
                }
            } else {
                activeInventory.setItem(buttonPosition, ItemWorker.SetMaterial(item, Material.PURPLE_WOOL));
                player.openInventory(LoanWindow.GetWindow(activeInventory, player, buttonPosition, true));
            }
        }
    }
}
