package economy.pcconomy.backend.npc.objects;

import economy.pcconomy.backend.npc.traits.Trader;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class TraderObject extends NpcObject {
    public TraderObject(Trader traderTrait) {
        super(traderTrait.Storage, traderTrait.Revenue, traderTrait.Margin,
                traderTrait.Cost, traderTrait.IsRanted, traderTrait.HomeTown, traderTrait.Owner, traderTrait.Term);
    }
}
