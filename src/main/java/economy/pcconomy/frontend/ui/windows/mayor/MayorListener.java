package economy.pcconomy.frontend.ui.windows.mayor;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.scripts.ItemManager;
import economy.pcconomy.frontend.ui.Window;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class MayorListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (Window.isThisWindow(event, player, "Меню")) {
            var option = ItemManager.getName(Objects.requireNonNull(event.getCurrentItem()));

            if (option.equals("Установить торговца"))
                PcConomy.GlobalNPC.buyNPC(player, LicenseType.Market, NPC.traderCost + NPC.traderCost * PcConomy.GlobalBank.VAT);
            if (option.equals("Установить кредитора"))
                PcConomy.GlobalNPC.buyNPC(player, LicenseType.Loan, NPC.loanerCost + NPC.loanerCost * PcConomy.GlobalBank.VAT);

            event.setCancelled(true);
        }
    }
}
