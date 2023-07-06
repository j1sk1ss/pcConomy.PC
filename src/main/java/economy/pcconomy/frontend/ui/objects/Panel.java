package economy.pcconomy.frontend.ui.objects;

import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;

import economy.pcconomy.frontend.ui.objects.interactive.Button;
import economy.pcconomy.frontend.ui.objects.interactive.IComponent;
import economy.pcconomy.frontend.ui.objects.interactive.Slider;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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

            if (component instanceof Button button) {
                var text = button.getName();
                var lore = button.getLore();

                for (var coordinate : coordinates)
                    inventory.setItem(coordinate, new Item(text, lore, Material.PAPER, 1, 17000)); //TODO: DATA MODEL
            } else if (component instanceof Slider slider) {
                for (var i = 0; i < coordinates.size(); i++)
                    inventory.setItem(coordinates.get(i), slider.getSlider().get(i));
            }
        }

        return inventory;
    }

    /**
     * Place buttons body to new inventory
     * @param inventory Inventory where should be placed buttons
     * @param customLore If u need to use custom lore
     * @return Inventory with buttons body
     */
    public Inventory placeComponents(Inventory inventory, List<String> customLore) {
        for (var component = 0; component < iComponents.size(); component++) {
            var coordinates = iComponents.get(component).getCoordinates();

            if (iComponents.get(component) instanceof Button button) {
                var text = button.getName();

                for (var coordinate : coordinates)
                    inventory.setItem(coordinate, new Item(text, customLore.get(component), Material.PAPER, 1, 17000)); //TODO: DATA MODEL
            } else if (iComponents.get(component) instanceof Slider slider) {
                for (var i = 0; i < coordinates.size(); i++)
                    inventory.setItem(coordinates.get(i), slider.getSlider().get(i));
            }
        }

        return inventory;
    }

    /**
     * Get sliders from panel
     * @return Sliders
     */
    public List<Slider> getSliders() {
        var sliders = new ArrayList<Slider>();
        for (var component : iComponents)
            if (component instanceof Slider slider) sliders.add(slider);

        return sliders;
    }

    /**
     * Get buttons from panel
     * @return Buttons
     */
    public List<Button> getButtons() {
        var buttons = new ArrayList<Button>();
        for (var component : iComponents)
            if (component instanceof Button button) buttons.add(button);

        return buttons;
    }
}
