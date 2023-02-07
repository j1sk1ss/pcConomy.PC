package economy.pcconomy.trade.objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Dictionary;
import java.util.List;
import java.util.UUID;

public class TraderObject {

    public TraderObject(String townName, double margin, Player owner) {
        this.margin = margin;
        this.townName = townName;
        this.owner = owner;
        uuid = new UUID(1,4);
    }

    public String townName;
    public Player owner;
    public double margin;
    public UUID uuid;
    public Dictionary<ItemStack, Integer> storage;
    public List<ItemStack> moneys;


}
