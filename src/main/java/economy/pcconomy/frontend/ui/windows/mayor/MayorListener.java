package economy.pcconomy.frontend.ui.windows.mayor;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.npc.NpcManager;

import economy.pcconomy.frontend.ui.windows.IWindowListener;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;


public class MayorListener implements IWindowListener {
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        switch (MayorWindow.Panel.click(event.getSlot()).getName()) {
            case "Установить торговца" -> PcConomy.GlobalNPC.buyNPC(player, LicenseType.Market,
                            NpcManager.traderCost + NpcManager.traderCost * PcConomy.GlobalBank.VAT);
            case "Установить кредитора" -> PcConomy.GlobalNPC.buyNPC(player, LicenseType.Loan,
                            NpcManager.loanerCost + NpcManager.loanerCost * PcConomy.GlobalBank.VAT);
        }
    }
}
