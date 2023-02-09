package economy.pcconomy.backend.trade.scripts;

import economy.pcconomy.backend.trade.npc.Trader;
import org.bukkit.entity.Player;

import java.util.List;

public class TraderWorker {

    public static List<Trader> traders;

    public static void CreateTrader(Player creator, double price) {
        traders.add(new Trader(creator, price));
    }

    public static Trader GetTrader(Player player) {
        for (Trader trader:
             traders) {
            if (trader.Owner.equals(player)) return trader;
        }

        return null;
    }

    public static Trader GetTrader(String name) {
        for (Trader trader:
                traders) {
            if (trader.getNPC().getFullName().equals(name)) return trader;
        }

        return null;
    }

    public static void SetTrader(Trader newTrader) {
        for (Trader trader:
                traders) {
            if (trader.getNPC().getFullName().equals(newTrader.getNPC().getFullName())) {
                traders.remove(trader);
                traders.add(newTrader);
            }
        }
    }
}
