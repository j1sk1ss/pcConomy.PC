package economy.pcconomy.backend.trade;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class TraderObject {
    public TraderObject(List<ItemStack> storage, double revenue, double margin, double cost, boolean isRanted,
                        String homeTown, UUID owner, String term) {
        Storage       = storage;
        Revenue       = revenue;
        Margin        = margin;
        Cost          = cost;
        this.isRanted = isRanted;
        this.homeTown = homeTown;
        Owner         = owner;
        Term          = term;
    }

    public final List<ItemStack> Storage;
    public final double Revenue;
    public final double Margin;
    public final double Cost;
    public final boolean isRanted;
    public final String homeTown;
    public final UUID Owner;
    public final String Term;
}
