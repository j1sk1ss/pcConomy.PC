package economy.pcconomy.frontend.ui.objects.interactive;

import org.bukkit.inventory.Inventory;

import java.util.List;

public interface IComponent {
    Inventory place(Inventory inventory);
    boolean isClicked(int click);

    String getName();
    String getLore();
    List<Integer> getCoordinates();
}
