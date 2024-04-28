package economy.pcconomy.frontend.ui.windows.shareholder;

import economy.pcconomy.frontend.ui.windows.IWindowListener;

import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.j1sk1ss.itemmanager.manager.Manager;

import java.util.UUID;


@ExtensionMethod({Manager.class})
public class ShareholderListener implements IWindowListener {
    @SuppressWarnings("deprecation")
    public void onClick(InventoryClickEvent event) {
        var windowTitle = event.getView().getTitle();
        var player = (Player)event.getWhoClicked();

        if (windowTitle.contains("Акции-Список")) {
            var item = event.getCurrentItem();
            if (item == null) return;

            var townId = UUID.fromString(item.getLoreLines().get(3).split(" ")[1]);
            player.openInventory(ShareholderWindow.acceptWindow(player, townId));

            event.setCancelled(true);
        }
    }
}
