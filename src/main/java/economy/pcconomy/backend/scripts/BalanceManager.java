package economy.pcconomy.backend.scripts;

import economy.pcconomy.PcConomy;
import me.yic.xconomy.api.XConomyAPI;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class BalanceManager {

    private final XConomyAPI xConomyAPI = PcConomy.xConomyAPI;

    /***
     * Give value of balance of Player
     * @param player Player that balance should be revealed
     * @return Balance
     */
    public double getBalance(Player player) {
        return xConomyAPI.getPlayerData(player.getUniqueId()).getBalance().doubleValue();
    }

    /***
     * Check is player have moneys
     * @param value Value
     * @param player Player
     * @return Status
     */
    public boolean notSolvent(double value, Player player) {
        return xConomyAPI.getPlayerData(player.getUniqueId()).getBalance().compareTo(new BigDecimal(value)) < 0;
    }

    /***
     * Gives money for player
     * @param amount Amount of giving
     * @param player Player that will take this amount
     */
    public void giveMoney(double amount, Player player) {
        xConomyAPI.changePlayerBalance(player.getUniqueId(), player.getName(), new BigDecimal(amount), true);
    }

    /***
     * Takes money from player
     * @param amount Amount of taken money
     * @param player Player that will lose moneys
     */
    public void takeMoney(double amount, Player player) {
        if (notSolvent(amount, player)) return;
        xConomyAPI.changePlayerBalance(player.getUniqueId(), player.getName(), new BigDecimal(amount), false);
    }

}
