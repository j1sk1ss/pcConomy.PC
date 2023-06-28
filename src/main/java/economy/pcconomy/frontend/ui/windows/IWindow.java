package economy.pcconomy.frontend.ui.windows;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface IWindow {
    /**
     * Method for getting inventory window
     * @param player Player is an object, that call generation
     * @return Inventory object
     */
    Inventory generateWindow(Player player);
}
