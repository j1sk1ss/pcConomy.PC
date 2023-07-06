package economy.pcconomy.frontend.ui.windows.loans.loan;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;

import economy.pcconomy.backend.economy.bank.scripts.LoanManager;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.ItemManager;

import economy.pcconomy.frontend.ui.windows.Window;
import economy.pcconomy.frontend.ui.windows.loans.npcLoan.NPCLoanWindow;
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
            var currentTown = PcConomy.GlobalTownWorker.getTown(Objects.requireNonNull(town).getName());
            var buttonPosition = event.getSlot();

            if (event.getView().getTitle().contains("Город-Взятие")) {
                if (ItemManager.getName(Objects.requireNonNull(item)).contains(CashManager.currencySigh)) {
                    boolean isSafe = ItemManager.getLore(item).get(0).contains("Банк одобрит данный займ.");
                    final int maxCreditCount = 5;

                    if (isSafe && currentTown.getCreditList().size() < maxCreditCount) {
                        if (!currentTown.getCreditList().contains(LoanManager.getLoan(player.getUniqueId(), currentTown))) {
                            activeInventory.setItem(buttonPosition, ItemManager.setMaterial(item, Material.LIGHT_BLUE_WOOL));
                            LoanManager.createLoan(LoanWindow.getSelectedAmount(activeInventory),
                                    LoanWindow.getSelectedDuration(activeInventory), player, currentTown);
                            player.closeInventory();
                        }
                    }
                } else {
                    activeInventory.setItem(buttonPosition, ItemManager.setMaterial(item, Material.PURPLE_WOOL));
                    player.openInventory(new LoanWindow().regenerateWindow(activeInventory, player, buttonPosition, false));
                }

                return;
            }

            event.setCancelled(true);

            switch (NPCLoanWindow.Panel.click(buttonPosition).getName()) {
                case "Взять кредит" -> player.openInventory(new LoanWindow().takeWindow(player));
                case "Погасить кредит" -> {
                    LoanManager.payOffADebt(player, currentTown);
                    player.closeInventory();
                }
            }
        }
    }
}
