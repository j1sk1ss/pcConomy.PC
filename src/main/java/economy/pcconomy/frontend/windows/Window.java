package economy.pcconomy.frontend.windows;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;


public abstract class Window {
    /**
     * Method for checking owning of this window
     * @param event Click event in some inventory
     * @param player Player that generate event
     * @param windowName Window name
     * @return Status of owning this window
     */
    @SuppressWarnings("deprecation")
    public static boolean isThisWindow(InventoryClickEvent event, Player player, String windowName){
        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player currentPlayer)
                return event.getView().getTitle().contains(windowName) && currentPlayer.equals(player);

        return false;
    }
}