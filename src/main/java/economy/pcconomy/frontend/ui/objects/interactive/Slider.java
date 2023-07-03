package economy.pcconomy.frontend.ui.objects.interactive;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Slider {
    public Slider(List<Integer> coordinates, List<ItemStack> slider,
                  Material chosenOption, Material defaultOption) {
        this.coordinates   = coordinates;
        this.slider        = slider;
        this.chosenOption  = chosenOption;
        this.defaultOption = defaultOption;
    }

    private final List<Integer> coordinates;
    private final List<ItemStack> slider;
    private final Material chosenOption;
    private final Material defaultOption;

    public List<ItemStack> getSlider() {
        for (var i = 0; i < coordinates.size(); i++)
            slider.get(i).setType(defaultOption);

        return slider;
    }

    public List<ItemStack> getSlider(int chose) {
        for (var i = 0; i < coordinates.size(); i++)
            slider.get(i).setType(i == chose ? chosenOption : defaultOption);

        return slider;
    }

    public ItemStack getChose() {
        for (var item : slider)
            if (item.getType().equals(chosenOption)) return item;

        return null;
    }
}
