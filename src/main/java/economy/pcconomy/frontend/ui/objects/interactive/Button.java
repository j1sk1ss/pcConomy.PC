package economy.pcconomy.frontend.ui.objects.interactive;

import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Button object
 * @param firstSlot First coordinate of button
 * @param secondSlot Second coordinate of button
 * @param name Name of button
 */
public record Button(int firstSlot, int secondSlot, String name, String lore) implements IComponent {
    /**
     * Checks click status of button
     * @param click Click coordinate
     * @return Status of click
     */
    public boolean isClicked(int click) {
        return getCoordinates().contains(click);
    }

    public String getName() {
        return name();
    }

    public String getLore() {
        return lore();
    }

    public List<Integer> getCoordinates() {
        var list = new ArrayList<Integer>();

        var height = firstSlot() / 9;
        var weight = secondSlot() % 9;
        for (var i = 0; i < height; i++)
            for (var j = 0; j < weight; j++)
                list.add(height * i + j);

        return list;
    }

    /**
     * Place button in inventory
     * @param inventory Inventory where should be placed button
     */
    public void place(Inventory inventory) { //TODO: DATA MODEL
        for (var coordinate : getCoordinates())
            inventory.setItem(coordinate, new Item(name(), lore(), Material.PAPER, 1, 17000));
    }

    /**
     * Displace button in inventory
     * @param inventory Inventory where should be displaced button
     */
    public void displace(Inventory inventory) {
        for (var coordinate : getCoordinates())
            if (inventory.getItem(coordinate) != null)
                if (ItemManager.getName(Objects.requireNonNull(inventory.getItem(coordinate))).equals(name()))
                    inventory.setItem(coordinate, null);
    }
}