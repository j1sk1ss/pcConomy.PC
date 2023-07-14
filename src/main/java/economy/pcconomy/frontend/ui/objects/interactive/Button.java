package economy.pcconomy.frontend.ui.objects.interactive;

import economy.pcconomy.backend.scripts.items.Item;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Button object
 * @param coordinates Coordinates of button
 * @param name Name of button
 */
public record Button(List<Integer> coordinates, String name, String lore) implements IComponent {
    /**
     * Checks click status of button
     * @param click Click coordinate
     * @return Status of click
     */
    public boolean isClicked(int click) {
        return coordinates().contains(click);
    }

    public String getName() {
        return name();
    }

    public String getLore() {
        return lore();
    }

    public List<Integer> getCoordinates() {
        return coordinates();
    }

    /**
     * Place button in inventory
     * @param inventory Inventory where should be placed button
     * @return Inventory with placed button
     */
    public Inventory place(Inventory inventory) { //TODO: DATA MODEL
        for (var coordinate : coordinates())
            inventory.setItem(coordinate, new Item(name(), lore(), Material.PAPER, 1, 17000));

        return inventory;
    }
}