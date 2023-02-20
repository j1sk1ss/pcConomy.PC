package economy.pcconomy.frontend.ui.windows.loan;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;

import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.scripts.ItemWorker;

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

        if (item != null) {
            if (activeInventory.getHolder() instanceof Player player1)
                if (player1.equals(player)) {
                    var title = event.getView().getTitle();
                    var town = TownyAPI.getInstance().getTown(player.getLocation());
                    var townObject = PcConomy.GlobalTownWorker.GetTownObject(town.getName());
                    boolean canReadHistory =
                            !PcConomy.GlobalLicenseWorker.isOverdue(PcConomy.GlobalLicenseWorker
                                    .GetLicense(town.getMayor().getPlayer(), LicenseType.LoanHistory));
                    var buttonPosition = event.getSlot();
                    event.setCancelled(true);

                    if (title.contains("Кредит-Город")) {
                        if (ItemWorker.GetName(item).contains("Выплатить кредит")) {
                            var balanceWorker = new BalanceWorker();
                            var loanAmount = townObject.GetLoan(player.getUniqueId()).amount;

                            if (!balanceWorker.isSolvent(loanAmount, player)) {
                                balanceWorker.TakeMoney(loanAmount, player);
                                townObject.changeBudget(loanAmount);
                                townObject.DestroyLoan(player.getUniqueId());

                                player.openInventory(LoanWindow.GetNPCLoanWindow(player, canReadHistory));
                            }
                            return;
                        }

                        if (ItemWorker.GetName(item).contains(CashWorker.currencySigh)) {
                            boolean isSafe = ItemWorker.GetLore(item).contains("Банк одобрит данный займ.");

                            if (isSafe) {
                                if (!townObject.Credit.contains(townObject.GetLoan(player.getUniqueId()))) {
                                    activeInventory.setItem(buttonPosition, ItemWorker.SetMaterial(item, Material.LIGHT_BLUE_WOOL));
                                    townObject.CreateLoan(LoanWindow.GetSelectedAmount(activeInventory),
                                            LoanWindow.GetSelectedDuration(activeInventory), player);
                                    player.closeInventory();
                                }
                            }
                        } else {
                            activeInventory.setItem(buttonPosition, ItemWorker.SetMaterial(item, Material.PURPLE_WOOL));
                            player.openInventory(LoanWindow.GetNPCLoanWindow(activeInventory, player, buttonPosition, canReadHistory));
                        }
                    }
                }

        }
    }
}
