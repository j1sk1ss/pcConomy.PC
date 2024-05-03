package economy.pcconomy.frontend.windows;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public abstract class WindowListener {
    public void onClick(InventoryClickEvent event) {
        return;
    }

    public void onInteract(PlayerInteractEvent event) {
        return;
    }
}
