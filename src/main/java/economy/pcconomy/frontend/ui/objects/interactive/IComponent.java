package economy.pcconomy.frontend.ui.objects.interactive;

import org.bukkit.inventory.Inventory;

import java.util.List;

public interface IComponent {
    void place(Inventory inventory);
    void displace(Inventory inventory);
    boolean isClicked(int click);

    String getName();
    String getLoreLines();
    List<Integer> getCoordinates();
}
