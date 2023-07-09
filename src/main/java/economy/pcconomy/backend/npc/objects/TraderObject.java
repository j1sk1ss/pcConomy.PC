package economy.pcconomy.backend.npc.objects;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class TraderObject implements INpcObject {
    public TraderObject(List<ItemStack> storage, double revenue, double margin, double cost, boolean isRanted,
                        UUID homeTown, UUID owner, String term) {
        Storage       = storage;
        Revenue       = revenue;
        Margin        = margin;
        Cost          = cost;
        this.IsRanted = isRanted;
        this.HomeTown = homeTown;
        Owner         = owner;
        Term          = term;
    }

    public final List<ItemStack> Storage;
    public final double Revenue;
    public final double Margin;
    public final double Cost;
    public final boolean IsRanted;
    public final UUID HomeTown;
    public final UUID Owner;
    public final String Term;

    @Override
    public INpcObject getBaseClass() {
        return this;
    }
}
