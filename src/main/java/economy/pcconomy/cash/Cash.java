package economy.pcconomy.cash;

import economy.pcconomy.scripts.BalanceWorker;
import economy.pcconomy.scripts.CashWorker;
import org.bukkit.entity.Player;

public class Cash {

    public void TakeCash(double amount, Player player) {
        CashWorker.GiveCash(amount, player);
    }

    public void PutCash(double amount, Player player) {

    }

}
