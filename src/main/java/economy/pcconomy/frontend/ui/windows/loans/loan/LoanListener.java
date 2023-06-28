package economy.pcconomy.frontend.ui.windows.loans.loan;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;

import economy.pcconomy.backend.bank.scripts.LoanManager;
import economy.pcconomy.backend.cash.scripts.CashManager;
import economy.pcconomy.backend.scripts.ItemManager;

import economy.pcconomy.frontend.ui.Window;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class LoanListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var activeInventory = event.getInventory();
        var item = event.getCurrentItem();

        if (Window.isThisWindow(event, player, "Кредит-Город")) {
            var town = TownyAPI.getInstance().getTown(player.getLocation());
            var townObject = PcConomy.GlobalTownWorker.getTownObject(Objects.requireNonNull(town).getName());
            var buttonPosition = event.getSlot();
            event.setCancelled(true);

            if (ItemManager.getName(item).contains("Выплатить кредит")) {
                LoanManager.payOffADebt(player, townObject);
                player.closeInventory();
            }

            if (ItemManager.getName(item).contains(CashManager.currencySigh)) {
                boolean isSafe = ItemManager.getLore(item).contains("Банк одобрит данный займ.");
                final int maxCreditCount = 5;

                if (isSafe && townObject.Credit.size() < maxCreditCount) {
                    if (!townObject.Credit.contains(LoanManager.getLoan(player.getUniqueId(), townObject))) {
                        activeInventory.setItem(buttonPosition, ItemManager.setMaterial(item, Material.LIGHT_BLUE_WOOL));
                        LoanManager.createLoan(LoanWindow.getSelectedAmount(activeInventory),
                                LoanWindow.getSelectedDuration(activeInventory), player, townObject);
                        player.closeInventory();
                    }
                }
            } else {
                activeInventory.setItem(buttonPosition, ItemManager.setMaterial(item, Material.PURPLE_WOOL));
                player.openInventory(new LoanWindow().regenerateWindow(activeInventory, player, buttonPosition, false));
            }
        }
    }
}
