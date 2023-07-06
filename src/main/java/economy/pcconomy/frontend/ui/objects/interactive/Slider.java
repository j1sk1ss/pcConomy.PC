package economy.pcconomy.frontend.ui.objects.interactive;

import economy.pcconomy.backend.scripts.items.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Slider implements IComponent {
    /**
     * Slider component
     * @param coordinates Coordinates of slider
     * @param slider Slider default body
     * @param chosenOption Material of chosen option
     * @param defaultOption Material of default option
     */
    public Slider(List<Integer> coordinates, List<ItemStack> slider,
                  int chosenOption, int defaultOption) {
        this.coordinates   = coordinates;
        this.slider        = slider;
        this.chosenOption  = chosenOption;
        this.defaultOption = defaultOption;
    }

    /**
     * Deep copy of slider
     * @param slider Slider that will be copied
     */
    public Slider(Slider slider) {
        this.coordinates   = new ArrayList<>(slider.coordinates);
        this.slider        = new ArrayList<>(slider.slider);

        this.chosenOption  = slider.chosenOption;
        this.defaultOption = slider.defaultOption;
    }

    private final List<Integer> coordinates;
    private final List<ItemStack> slider;
    private final int chosenOption;
    private final int defaultOption;

    public String getName() {
        return "Slider";
    }

    public String getLore() {
        return "Slider";
    }

    public List<Integer> getCoordinates() {
        return coordinates;
    }

    /**
     * Get default slider
     * @return Slider
     */
    public List<ItemStack> getSlider() {
        return slider;
    }

    /**
     * Set slider with chose
     * @param chose Coordinate of chose
     */
    public void setChose(int chose) {
        for (var i = 0; i < coordinates.size(); i++)
            slider.set(i, new Item(slider.get(i), i == chose ? chosenOption : defaultOption));
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
     * @return Inventory with slider
     */
    public Inventory place(Inventory inventory) {
        for (var coordinate = 0; coordinate < coordinates.size(); coordinate++)
            inventory.setItem(coordinate, slider.get(coordinate));

        return inventory;
    }

    /**
     * Get coordinate of chose
     * @return Coordinate of chose
     */
    public ItemStack getChose() {
        for (var item : slider)
            if (item.getItemMeta().getCustomModelData() == chosenOption) return item;

        return null;
    }
}
