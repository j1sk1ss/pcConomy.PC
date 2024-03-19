package economy.pcconomy.backend.npc.objects;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;


public class NpcObject {
    public NpcObject(List<ItemStack> storage, double revenue, double margin, double cost, boolean isRanted,
                     UUID homeTown, UUID owner, String term) {
        Storage  = storage;
        Revenue  = revenue;
        Margin   = margin;
        Cost     = cost;
        IsRanted = isRanted;
        HomeTown = homeTown;
        Owner    = owner;
        Term     = term;
    }

    public final List<ItemStack> Storage;
    public final double Revenue;
    public final double Margin;
    public final double Cost;
    public final boolean IsRanted;
    public final UUID HomeTown;
    public final UUID Owner;
    public final String Term;
}
