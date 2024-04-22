package economy.pcconomy.frontend.ui.windows.mayor;

import economy.pcconomy.frontend.ui.windows.IWindowListener;
import org.bukkit.event.inventory.InventoryClickEvent;


public class MayorListener implements IWindowListener {
    public void onClick(InventoryClickEvent event) {
        MayorWindow.Panel.click(event);
    }
}
