package economy.pcconomy.frontend.ui.windows.mayor.manager;

import economy.pcconomy.frontend.ui.windows.IWindowListener;

import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.j1sk1ss.itemmanager.manager.Manager;


@ExtensionMethod({Manager.class})
public class MayorManagerListener implements IWindowListener {
    public void onClick(InventoryClickEvent event) {
        var player = (Player)event.getWhoClicked();
        var inventory = event.getInventory();
        player.openInventory(MayorManagerWindow.generateTradeControls(player, Integer.parseInt(inventory.getItem(event.getSlot()).getName())));
    }
}
