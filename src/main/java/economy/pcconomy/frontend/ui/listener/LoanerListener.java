package economy.pcconomy.frontend.ui.listener;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.frontend.ui.windows.LoanWindow;
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
        var item = event.getCurrentItem();

        if (item != null) {
            if (event.getInventory().getHolder() instanceof Player player1)
                if (event.getView().getTitle().equals("Кредит") && player1.equals(player)) {
                    var buttonPosition = event.getSlot();

                    if (ItemWorker.GetName(item).contains("Выплатить кредит")) {
                        var balanceWorker = new BalanceWorker();
                        if (balanceWorker.isSolvent(PcConomy.GlobalBank.Credit.get(player).amount, player)) return;

                        balanceWorker.TakeMoney(PcConomy.GlobalBank.Credit.get(player).amount, player);
                        PcConomy.GlobalBank.BankBudget += PcConomy.GlobalBank.Credit.get(player).amount;
                        PcConomy.GlobalBank.DestroyLoan(player);
                    }

                    if (ItemWorker.GetName(item).contains(CashWorker.currencySigh)) {
                        event.getInventory().setItem(buttonPosition, ItemWorker.SetMaterial(item, Material.LIGHT_BLUE_WOOL));
                        boolean isSafe = ItemWorker.GetLore(item).contains("Банк одобрит данный займ.");

                        if (isSafe) {
                            if (PcConomy.GlobalBank.Credit.get(player) == null) {
                                PcConomy.GlobalBank.CreateLoan(LoanWindow.GetSelectedAmount(event.getInventory()),
                                        LoanWindow.GetSelectedDuration(event.getInventory()), player);
                                Close(event);
                            }
                        }
                    } else {
                        event.getInventory().setItem(buttonPosition, ItemWorker.SetMaterial(item, Material.PURPLE_WOOL));
                        player.openInventory(LoanWindow.GetLoanWindow(event.getInventory(), player, buttonPosition));
                    }
                    event.setCancelled(true);
                }
        }
    }

    private void Close(InventoryClickEvent event) {
        event.setCancelled(true);
        event.getWhoClicked().closeInventory();
    }
}
