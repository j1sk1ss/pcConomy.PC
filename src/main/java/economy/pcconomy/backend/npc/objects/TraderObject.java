package economy.pcconomy.backend.npc.objects;

import economy.pcconomy.backend.npc.traits.Trader;

public class TraderObject extends NpcObject {
    public TraderObject(Trader traderTrait) {
        super(traderTrait.Storage, traderTrait.Revenue, traderTrait.Margin,
                traderTrait.Cost, traderTrait.IsRanted, traderTrait.HomeTown, traderTrait.Owner, traderTrait.Term, traderTrait.Level);
    }
}
