package economy.pcconomy.frontend.ui.objects.interactive;

import economy.pcconomy.backend.scripts.ItemManager;
import org.bukkit.Material;
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
                  Material chosenOption, Material defaultOption) {
        this.coordinates   = coordinates;
        this.slider        = slider;
        this.chosenOption  = chosenOption;
        this.defaultOption = defaultOption;
    }

    public Slider(Slider slider) {
        this.coordinates   = new ArrayList<>(slider.coordinates);
        this.slider        = new ArrayList<>(slider.slider);

        this.chosenOption  = slider.chosenOption;
        this.defaultOption = slider.defaultOption;
    }

    private final List<Integer> coordinates;
    private final List<ItemStack> slider;
    private final Material chosenOption;
    private final Material defaultOption;

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
        for (var i = 0; i < coordinates.size(); i++) {
            slider.set(i, ItemManager.setLore(slider.get(i),"NOT CHOSE"));
            slider.get(i).setType(defaultOption);
        }

        return slider;
    }

    /**
     * Get slider with chose
     * @param chose Coordinate of chose
     * @return Slider
     */
    public List<ItemStack> getSlider(int chose) {
        for (var i = 0; i < coordinates.size(); i++) {
            slider.set(i, ItemManager.setLore(slider.get(i), i == chose ? "CHOSE" : "NOT CHOSE"));
            slider.get(i).setType(i == chose ? chosenOption : defaultOption);
        }

        return slider;
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
            if (item.getType().equals(chosenOption)) return item;

        return null;
    }
}
