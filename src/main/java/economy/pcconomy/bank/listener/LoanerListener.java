package economy.pcconomy.bank.listener;

import economy.pcconomy.PcConomy;
import economy.pcconomy.scripts.ItemWorker;
import economy.pcconomy.ui.windows.LoanWindow;
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

                    if (ItemWorker.GetName(item).contains("$")) {
                        event.getInventory().setItem(buttonPosition, ItemWorker.SetMaterial(item, Material.LIGHT_BLUE_WOOL));
                        boolean isSafe = ItemWorker.GetLore(item).contains("Банк одобрит данный займ.");

                        if (isSafe) {
                            if (PcConomy.GlobalBank.Credit.get(player) == null) {
                                PcConomy.GlobalBank.CreateLoan(LoanWindow.GetAmount(event.getInventory()),
                                        LoanWindow.GetDuration(event.getInventory()), player);
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
