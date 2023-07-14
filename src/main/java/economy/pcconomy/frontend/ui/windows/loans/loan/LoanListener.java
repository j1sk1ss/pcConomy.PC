package economy.pcconomy.frontend.ui.windows.loans.loan;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;

import economy.pcconomy.backend.economy.credit.scripts.LoanManager;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.npc.traits.Loaner;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;

import economy.pcconomy.frontend.ui.windows.Window;
import economy.pcconomy.frontend.ui.windows.loans.npcLoan.NPCLoanWindow;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.Objects;

public class LoanListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var activeInventory = event.getInventory();
        var item = event.getCurrentItem();

        if (Window.isThisWindow(event, player, "Кредит-Город")) {
            var town = TownyAPI.getInstance().getTown(player.getLocation());
            var currentTown = PcConomy.GlobalTownManager.getTown(Objects.requireNonNull(town).getUUID());
            var buttonPosition = event.getSlot();

            var title = event.getView().getTitle();
            var loaner = getLoanerFromTitle(title);

            if (title.contains("Город-Взятие")) {
                if (ItemManager.getName(Objects.requireNonNull(item)).contains(CashManager.currencySigh)) {
                    var isSafe = ItemManager.getLore(item).get(0).contains("Город одобрит данный займ.");
                    final int maxCreditCount = 5;

                    if (isSafe && currentTown.getCreditList().size() < maxCreditCount) {
                        if (!currentTown.getCreditList().contains(LoanManager.getLoan(player.getUniqueId(), currentTown))) {
                            activeInventory.setItem(buttonPosition, new Item(item, Material.LIGHT_BLUE_WOOL));
                            var amount = LoanWindow.getSelectedAmount(activeInventory);

                            Objects.requireNonNull(loaner).Pull -= amount;
                            LoanManager.createLoan(amount, LoanWindow.getSelectedDuration(activeInventory), player, currentTown);

                            player.closeInventory();
                        }
                    }
                } else {
                    activeInventory.setItem(buttonPosition, new Item(item, Material.PURPLE_WOOL));
                    player.openInventory(new LoanWindow(loaner).regenerateWindow(activeInventory, player, buttonPosition));
                }

                return;
            }

            event.setCancelled(true);

            switch (NPCLoanWindow.Panel.click(buttonPosition).getName()) {
                case "Взять кредит" -> player.openInventory(new LoanWindow(loaner).takeWindow(player));
                case "Погасить кредит" -> {
                    LoanManager.payOffADebt(player, currentTown);
                    player.closeInventory();
                }
            }
        }
    }

    private Loaner getLoanerFromTitle(String name) {
        try {
            if (Arrays.stream(name.split(" ")).toList().size() <= 1) return null;
            return PcConomy.GlobalNPC.getNPC(Integer.parseInt(name.split(" ")[1])).getOrAddTrait(Loaner.class);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
