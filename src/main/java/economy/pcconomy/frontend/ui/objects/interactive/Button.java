package economy.pcconomy.frontend.ui.objects.interactive;

import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Button object
 * @param firstSlot First coordinate of button
 * @param secondSlot Second coordinate of button
 * @param name Name of button
 */
public record Button(int firstSlot, int secondSlot, String name, String lore, Consumer<InventoryClickEvent> delegate) implements IComponent {
    public Button(int firstSlot, int secondSlot, String name, String lore) {
        this(firstSlot, secondSlot, name, lore, null);
    }

    public Button(int firstSlot, int secondSlot, String name) {
        this(firstSlot, secondSlot, name, "", null);
    }
    
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

    public String getLoreLines() {
        return lore();
    }

    public List<Integer> getCoordinates() {
        var list = new ArrayList<Integer>();
        var secondCoordinate = secondSlot() - firstSlot();

        var height = (secondCoordinate / 9) + 1;
        var weight = (secondCoordinate % 9) + 1;
        for (var i = firstSlot() / 9; i < firstSlot() / 9 + height; i++)
            for (var j = firstSlot() % 9; j < firstSlot() % 9 + weight; j++)
                list.add(9 * i + j);

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
                if (Objects.requireNonNull(ItemManager.getName(inventory.getItem(coordinate))).equals(name()))
                    inventory.setItem(coordinate, null);
    }

    /**
     * Execute delegate associated with the button
     */
    public void action(InventoryClickEvent event) {
        if (delegate != null) delegate.accept(event);
    }
}