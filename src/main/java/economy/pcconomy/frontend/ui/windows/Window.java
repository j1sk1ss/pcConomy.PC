package economy.pcconomy.frontend.ui.windows;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Window {
    /**
     * Open window method for opening custom inventories
     * @param player Player that should see this window
     * @param window Window type
     */
    public static void OpenWindow(Player player, IWindow window) {
        player.openInventory(window.generateWindow(player));
    }

    /**
     * Method for checking owning of this window
     * @param event Click event in some inventory
     * @param player Player that generate event
     * @param windowName Window name
     * @return Status of owning this window
     */
    public static boolean isThisWindow(InventoryClickEvent event, Player player, String windowName){
        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player currentPlayer)
                return event.getView().getTitle().contains(windowName) && currentPlayer.equals(player);

        return false;
    }
}
