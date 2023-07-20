package economy.pcconomy.frontend.ui.objects.interactive;

import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Slider implements IComponent {
    /**
     * Slider component
     * @param coordinates Coordinates of slider
     * @param options List of options
     */
    public Slider(List<Integer> coordinates, List<String> options, String lore, String name) {
        this.coordinates = coordinates;
        this.options = options;
        this.name = name;
        this.lore = lore;

        slider = new ArrayList<>();
        for (var option : options)
            slider.add(new Item(option, lore, Material.GLASS, 1, 17000));
    }

    /**
     * Deep copy of slider
     * @param slider Slider that will be copied
     */
    public Slider(Slider slider) {
        this.coordinates = new ArrayList<>(slider.coordinates);
        this.options = new ArrayList<>(slider.options);
        this.name = slider.getName();
        this.lore = slider.lore;

        this.slider = slider.slider;
    }

    private final List<Integer> coordinates;
    private final List<String> options;
    private final List<ItemStack> slider;
    private final String lore;
    private final String name;

    public String getName() {
        return name;
    }

    public String getLore() {
        return "Slider";
    }

    public List<Integer> getCoordinates() {
        return coordinates;
    }

    /**
     * Set slider with chose
     * @param chose Coordinate of chose
     */
    public void setChose(int chose) {
        for (var i = 0; i < coordinates.size(); i++) {
            slider.get(i).setType(coordinates.get(i) == chose ? Material.PURPLE_WOOL : Material.GLASS); //TODO: DATA MODEL
            slider.set(i, new Item(slider.get(i), coordinates.get(i) == chose ? 17000 : 17050));
        }
    }

    /**
     * Checks if this slider clicked
     * @param click Click position
     * @return Click status
     */
    public boolean isClicked(int click) {
        return coordinates.contains(click);
    }

    /**
     * Place slider into inventory
     * @param inventory Inventory where should be placed slider
     */
    public void place(Inventory inventory) {
        for (var coordinate = 0; coordinate < coordinates.size(); coordinate++)
            inventory.setItem(coordinates.get(coordinate), slider.get(coordinate));
    }

    /**
     * Displace slider in inventory
     * @param inventory Inventory where should be displaced slider
     */
    public void displace(Inventory inventory) {
        for (var coordinate = 0; coordinate < coordinates.size(); coordinate++)
            if (inventory.getItem(coordinate) != null)
                if (ItemManager.getName(Objects.requireNonNull(inventory.getItem(coordinate))).equals(getName()))
                    inventory.setItem(coordinate, null);
    }

    /**
     * Get coordinate of chose
     * @return Coordinate of chose
     */
    public ItemStack getChose() {
        for (var item : slider)
            if (item.getType() == Material.PURPLE_WOOL)
                return item;

        return null;
    }
}
