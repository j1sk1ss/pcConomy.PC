package economy.pcconomy.scripts;

import economy.pcconomy.PcConomy;
import me.yic.xconomy.api.XConomyAPI;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class BalanceWorker {

    private final XConomyAPI xConomyAPI = PcConomy.xConomyAPI;

    public boolean isSolvent(double price, Player player) {
        return xConomyAPI.getPlayerData(player.getUniqueId()).getBalance().compareTo(new BigDecimal(price)) > 0;
    }

    public void GiveMoney(double amount, Player player) {
        xConomyAPI.changePlayerBalance(player.getUniqueId(), player.getName(), new BigDecimal(amount), true);
    }

    public void TakeMoney(double amount, Player player) {
        if (!isSolvent(amount, player)) return;
        xConomyAPI.changePlayerBalance(player.getUniqueId(), player.getName(), new BigDecimal(amount), false);
    }

}
