package economy.pcconomy.trade;

import economy.pcconomy.PcConomy;
import economy.pcconomy.cash.Cash;
import economy.pcconomy.scripts.BalanceWorker;
import economy.pcconomy.town.scripts.TownWorker;
import economy.pcconomy.trade.objects.TraderObject;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Trade {

    public static List<TraderObject> traderObjects = new ArrayList<>();
    public final static double traderCost = 1000;

    public static void CreateTrader(String townName, Player player) {
        // Создание продовца
        var balanceWorker = new BalanceWorker();
        if (balanceWorker.isSolvent(traderCost, player)) return;

        balanceWorker.TakeMoney(traderCost, player);
        PcConomy.GlobalBank.BankBudget += traderCost;

        var townObj = TownWorker.GetTownObject(townName);
        if (townObj == null) return;

        traderObjects.add(new TraderObject(townName, townObj.Margin, player));
    }

    public static void TransferTrader(Player owner, Player buyer, UUID uuid, double price) {
        // Продажа продовца другому игроку
        TraderObject trader = new TraderObject(null, 0, null);

        for (TraderObject object:
             traderObjects) {
            if (object.uuid.equals(uuid)) trader = object;
        }

        if (!trader.owner.equals(owner)) return;
        // Подтверждение от владельца мб привязать

        if (new Cash().AmountOfCashInInventory(buyer) < price) return;
        new Cash().TakeCashFromInventory(price, buyer);
        TownWorker.GetTownObject(trader.townName).setBudget(TownWorker.GetTownObject(trader.townName).getBudget() + price);

        traderObjects.remove(trader);
        trader.owner = buyer;
        traderObjects.add(trader);
    }

    public static void DestroyTrader(UUID uuid, Player owner) {
        // Уничтожение продовца
        traderObjects.removeIf(trader -> trader.uuid.equals(uuid));
    }
}
