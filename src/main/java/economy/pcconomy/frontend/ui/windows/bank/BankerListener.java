package economy.pcconomy.frontend.ui.windows.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.scripts.items.ItemManager;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BankerListener {
    public static void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var option = event.getCurrentItem();
        if (option == null) return;

        if (ItemManager.getLore(option).size() < 2) return;
        var amount = ItemManager.getPriceFromLore(option, 1);

        if (amount > 0) PcConomy.GlobalBank.giveCashToPlayer(amount, player);
        else PcConomy.GlobalBank.takeCashFromPlayer(Math.abs(amount), player);
    }
}
