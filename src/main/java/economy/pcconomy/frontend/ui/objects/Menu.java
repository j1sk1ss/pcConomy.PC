package economy.pcconomy.frontend.ui.objects;

import org.bukkit.inventory.Inventory;

import java.util.List;

public class Menu {
    public Menu(List<Panel> panels) {
        MenuBody = panels;
    }

    public List<Panel> MenuBody;

    /**
     * Get panel by name
     * @param name Panel name
     * @return Panel
     */
    public Panel getPanel(String name) {
        for (var panel : MenuBody)
            if (panel.Name.contains(name))
                return panel;

        return null;
    }

    /**
     * Place panel in inventory
     * @param name Panel name
     * @param inventory Inventory
     */
    public void placePanel(String name, Inventory inventory) {
        getPanel(name).placeComponents(inventory);
    }

    /**
     * Displace panel in inventory
     * @param name Panel name
     * @param inventory Inventory
     */
    public void displacePanel(String name, Inventory inventory) {
        getPanel(name).displaceComponents(inventory);
    }
}
