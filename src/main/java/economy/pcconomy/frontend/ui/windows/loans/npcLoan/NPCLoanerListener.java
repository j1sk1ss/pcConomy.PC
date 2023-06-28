package economy.pcconomy.frontend.ui.windows.loans.npcLoan;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.bank.scripts.LoanManager;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.ItemManager;

import economy.pcconomy.frontend.ui.Window;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class NPCLoanerListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var activeInventory = event.getInventory();
        var item = event.getCurrentItem();

        if (item == null) return;

        if (Window.isThisWindow(event, player, "Кредит")) {
            var buttonPosition = event.getSlot();
            event.setCancelled(true);

            if (ItemManager.getName(item).contains("Выплатить кредит")) {
                LoanManager.payOffADebt(player, PcConomy.GlobalBank);
                player.closeInventory();

                return;
            }

            if (ItemManager.getName(item).contains(CashManager.currencySigh)) {
                boolean isSafe = ItemManager.getLore(item).get(0).contains("Банк одобрит данный займ.");

                if (isSafe)
                    if (!PcConomy.GlobalBank.Credit.contains(LoanManager.getLoan(player.getUniqueId(), PcConomy.GlobalBank))) {
                        activeInventory.setItem(buttonPosition, ItemManager.setMaterial(item, Material.LIGHT_BLUE_WOOL));
                        LoanManager.createLoan(NPCLoanWindow.getSelectedAmount(activeInventory),
                                NPCLoanWindow.getSelectedDuration(activeInventory), player, PcConomy.GlobalBank);
                        player.closeInventory();

                        return;
                    }
            } else {
                activeInventory.setItem(buttonPosition, ItemManager.setMaterial(item, Material.PURPLE_WOOL));
                player.openInventory(new NPCLoanWindow().regenerateWindow(activeInventory, player, buttonPosition, true));
            }
        }
    }
}
