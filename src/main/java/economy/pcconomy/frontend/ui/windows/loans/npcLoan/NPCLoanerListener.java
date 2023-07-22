package economy.pcconomy.frontend.ui.windows.loans.npcLoan;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.credit.scripts.LoanManager;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;

import economy.pcconomy.frontend.ui.windows.Window;
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

        if (Window.isThisWindow(event, player, "Кредит-Банк")) {
            var buttonPosition = event.getSlot();

            if (event.getView().getTitle().contains("Банк-Взятие")) {
                if (ItemManager.getName(item).contains(CashManager.currencySigh)) {
                    boolean isSafe = ItemManager.getLore(item).get(0).contains("Банк одобрит данный займ.");

                    if (isSafe)
                        if (!PcConomy.GlobalBank.Credit.contains(LoanManager.getLoan(player.getUniqueId(), PcConomy.GlobalBank))) {
                            LoanManager.createLoan(NPCLoanWindow.getSelectedAmount(activeInventory.getItem(buttonPosition)),
                                    NPCLoanWindow.getSelectedDuration(activeInventory), player, PcConomy.GlobalBank);
                            player.closeInventory();
                        }
                } else {
                    activeInventory.setItem(buttonPosition, new Item(item, Material.PURPLE_WOOL));
                    player.openInventory(new NPCLoanWindow().regenerateWindow(activeInventory, player, buttonPosition));
                }

                return;
            }

            switch (NPCLoanWindow.Panel.click(buttonPosition).getName()) {
                case "Взять кредит" -> player.openInventory(new NPCLoanWindow().takeWindow(player));
                case "Погасить кредит" -> {
                    LoanManager.payOffADebt(player, PcConomy.GlobalBank);
                    player.closeInventory();

                    return;
                }
            }

            event.setCancelled(true);
        }
    }
}
