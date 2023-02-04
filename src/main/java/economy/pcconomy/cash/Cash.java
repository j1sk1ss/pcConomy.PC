package economy.pcconomy.cash;

import economy.pcconomy.scripts.CashWorker;
import economy.pcconomy.scripts.ChangeWorker;
import economy.pcconomy.scripts.ExtraditionWorker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Cash {

    public void WithdrawCash(double amount, Player player) {
        CashWorker.WithdrawCash(amount, player);
    }

    public void PutCash(Player player) {
        new CashWorker().TakeCash(player);
    }

    public void GetChangeToPlayer(double amount, Player player) {
        List<ItemStack> change = ChangeWorker.getChangeInCash(amount);
        ExtraditionWorker.giveItems(change, player);
    }

}
