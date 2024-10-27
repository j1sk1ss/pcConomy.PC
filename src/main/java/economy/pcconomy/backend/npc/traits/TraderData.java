package economy.pcconomy.backend.npc.traits;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;


/**
 * This class designed for storing complex data of Trait class
 */
@Setter @Getter
public class TraderData {
    public TraderData(Trader trader) {
        Storage     = trader.getStorage();
        SpecialList = trader.getSpecialList();
        Revenue     = trader.getRevenue();
        Margin      = trader.getMargin();
        Cost        = trader.getCost();
        Ranted      = trader.isRanted();
        Term        = trader.getTerm();
        HomeTown    = trader.getHomeTown();
        Owner       = trader.getOwner();
        Level       = trader.getLevel();
    }

    public TraderData(
            List<ItemStack> storage, List<UUID> specialList, double revenue, double margin, double cost,
            boolean isRanted, String term, UUID home, UUID owner, int level
    ) {
        Storage     = storage;
        SpecialList = specialList;
        Revenue     = revenue;
        Margin      = margin;
        Cost        = cost;
        Ranted      = isRanted;
        Term        = term;
        HomeTown    = home;
        Owner       = owner;
        Level       = level;
    }

    private List<ItemStack> Storage;
    private List<UUID> SpecialList;
    private double Revenue;
    private double Margin;
    private double Cost;
    private boolean Ranted;
    private String Term;
    private UUID HomeTown;
    private UUID Owner;
    private int Level;
}
