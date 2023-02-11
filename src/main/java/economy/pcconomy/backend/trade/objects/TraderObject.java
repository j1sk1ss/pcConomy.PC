package economy.pcconomy.backend.trade.objects;

import economy.pcconomy.backend.trade.npc.Trader;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TraderObject {

    public TraderObject(List<ItemStack> storage, double revenue, double margin, double cost, boolean isRanted,
                        String homeTown, UUID owner) {
        Storage       = storage;
        Revenue       = revenue;
        Margin        = margin;
        Cost          = cost;
        this.isRanted = isRanted;
        this.homeTown = homeTown;
        Owner         = owner;
    }
    public List<ItemStack> Storage = new ArrayList<>();
    public double Revenue;
    public double Margin;
    public double Cost;
    public boolean isRanted;
    public String homeTown;
    public UUID Owner;

}
