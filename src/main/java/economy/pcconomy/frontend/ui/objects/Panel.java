package economy.pcconomy.frontend.ui.objects;

import economy.pcconomy.backend.scripts.items.Item;

import economy.pcconomy.frontend.ui.objects.events.ComponentClickEvent;
import economy.pcconomy.frontend.ui.objects.interactive.Button;
import economy.pcconomy.frontend.ui.objects.interactive.IComponent;
import economy.pcconomy.frontend.ui.objects.interactive.Slider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;


/**
 * Buttons panel for working with advanced UI
 */
@SuppressWarnings("ConstantConditions")
public class Panel {
    /**
     * Buttons panel
     * @param components Components of panel
     */
    public Panel(List<IComponent> components, String name) {
        Name        = name;
        iComponents = components;
    }


    public final String Name;
    private final List<IComponent> iComponents;

    
    /**
     * Get button what was clicked
     * @param click Slot of inventory what was clicked
     * @return Button what was clicked
     */
    public IComponent click(int click) {
        for (var component : iComponents) {
            if (component.isClicked(click)) {
                Bukkit.getPluginManager().callEvent(new ComponentClickEvent(component, null, click));
                return component;
            }
        }

        return null;
    }

    /**
     * Get button what was clicked
     * @param click Slot of inventory what was clicked
     * @return Button what was clicked
     */
    public IComponent click(int click, Player player) {
        for (var component : iComponents) {
            if (component.isClicked(click)) {
                Bukkit.getPluginManager().callEvent(new ComponentClickEvent(component, player, click));
                return component;
            }
        }

        return null;
    }

    /**
     * Get button what was clicked
     * @param click Slot of inventory what was clicked
     * @return Button what was clicked
     */
    public IComponent click(InventoryClickEvent click) {
        for (var component : iComponents) {
            if (component.isClicked(click.getSlot())) {
                Bukkit.getPluginManager().callEvent(new ComponentClickEvent(component, null, click.getSlot()));
                component.action(click);
                return component;
            }
        }

        return null;
    }

    /**
     * Place components body to new inventory
     * @param inventory Inventory where should be placed components
     * @return Inventory with components body
     */
    public Inventory placeComponents(Inventory inventory) {
        for (var component : iComponents) component.place(inventory);
        return inventory;
    }

    /**
     * Place components body to new inventory
     * @param inventory Inventory where should be placed components
     * @param customLore If you need to use custom lore
     * @return Inventory with components body
     */
    public Inventory placeComponents(Inventory inventory, List<String> customLore) {
        for (var component = 0; component < iComponents.size(); component++) {
            var coordinates = iComponents.get(component).getCoordinates();

            if (iComponents.get(component) instanceof Button button) {
                var text = button.getName();
                for (var coordinate : coordinates) inventory.setItem(coordinate, new Item(text, customLore.get(component), Material.PAPER, 1, 17000)); //TODO: DATA MODEL
            } else if (iComponents.get(component) instanceof Slider slider) slider.place(inventory);
        }

        return inventory;
    }

    /**
     * Displace components body to new inventory
     * @param inventory Inventory where should be displaced components
     * @return Inventory with components body
     */
    public Inventory displaceComponents(Inventory inventory) {
        for (var component : iComponents) component.displace(inventory);
        return inventory;
    }

    /**
     * Get slider from panel by name
     * @param name Name of slider
     * @return Slider
     */
    public Slider getSliders(String name) {
        for (var component : iComponents)
            if (component instanceof Slider slider)
                if (slider.getName().equals(name))
                    return slider;

        return null;
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
     * Get button from panel by name
     * @param name Name of button
     * @return Button
     */
    public Button getButtons(String name) {
        for (var component : iComponents)
            if (component instanceof Button button)
                if (button.getName().equals(name))
                    return button;

        return null;
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
