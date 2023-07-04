package economy.pcconomy.frontend.ui.objects;

import economy.pcconomy.backend.scripts.ItemManager;

import economy.pcconomy.frontend.ui.objects.interactive.IComponent;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Buttons panel for working with advanced UI
 */
public class Panel {
    /**
     * Buttons panel
     * @param buttons Buttons of panel
     */
    public Panel(List<IComponent> buttons) {
        this.iComponents = buttons;
    }

    private final List<IComponent> iComponents;

    /**
     * Get button what was clicked
     * @param click Slot of inventory what was clicked
     * @return Button what was clicked
     */
    public IComponent click(int click) {
        for (var component : iComponents)
            if (component.isClicked(click)) return component;

        return null;
    }

    /**
     * Place buttons body to new inventory
     * @param inventory Inventory where should be placed buttons
     * @return Inventory with buttons body
     */
    public Inventory placeComponents(Inventory inventory) {
        for (var component : iComponents) {
            var coordinates = component.getCoordinates();
            var text = component.getName();
            var lore = component.getLore();

            for (var coordinate : coordinates)
                inventory.setItem(coordinate, ItemManager.setLore(ItemManager
                        .setName(new ItemStack(Material.PAPER, 1), text), lore));
        }

        return inventory;
    }
}
