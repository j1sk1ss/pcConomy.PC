package economy.pcconomy.frontend.ui.objects.interactive;

import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;
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
                  int chosenOption, int defaultOption, String name) {
        this.coordinates   = coordinates;
        this.slider        = slider;
        this.chosenOption  = chosenOption;
        this.defaultOption = defaultOption;

        this.name = name;
    }

    /**
     * Deep copy of slider
     * @param slider Slider that will be copied
     */
    public Slider(Slider slider) {
        this.coordinates   = new ArrayList<>(slider.coordinates);

        this.slider = new ArrayList<>();
        for (var item : slider.slider) {
            this.slider.add(new Item(ItemManager.getName(item), String.join("\n",
                    ItemManager.getLore(item)), item.getType(),1,17000));
        }

        this.chosenOption  = slider.chosenOption;
        this.defaultOption = slider.defaultOption;

        this.name = slider.getName();
    }

    private final List<Integer> coordinates;
    private final List<ItemStack> slider;
    private final int chosenOption;
    private final int defaultOption;
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
        for (var i = 0; i < coordinates.size(); i++) {
            //slider.set(i, new Item(slider.get(i), i == chose ? chosenOption : defaultOption));
            slider.get(i).setType(coordinates.get(i) == chose ? Material.PURPLE_WOOL : Material.GLASS);
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
     * @return Inventory with slider
     */
    public Inventory place(Inventory inventory) {
        for (var coordinate = 0; coordinate < coordinates.size(); coordinate++)
            inventory.setItem(coordinates.get(coordinate), slider.get(coordinate));

        return inventory;
    }

    /**
     * Get coordinate of chose
     * @return Coordinate of chose
     */
    public ItemStack getChose() {
        for (var item : slider)
            if (item.getType() == Material.PURPLE_WOOL)
                //if (item.getItemMeta().getCustomModelData() == chosenOption)
                return item;

        return null;
    }
}
