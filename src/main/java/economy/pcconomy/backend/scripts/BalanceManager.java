package economy.pcconomy.backend.scripts;

import me.yic.xconomy.api.XConomyAPI;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class BalanceManager { // TODO: Think about writing own abstract economy overrider instead xConomy
    private static final XConomyAPI xConomyAPI = new XConomyAPI();

    /**
     * Give value of balance of Player
     * @param player Player that balance should be revealed
     * @return Balance
     */
    public double getBalance(Player player) {
        return xConomyAPI.getPlayerData(player.getUniqueId()).getBalance().doubleValue();
    }

    /**
     * Check is player have moneys
     * @param value Value
     * @param player Player
     * @return Status
     */
    public boolean solvent(double value, Player player) {
        return xConomyAPI.getPlayerData(player.getUniqueId()).getBalance().compareTo(new BigDecimal(value)) < 0;
    }

    /**
     * Gives money for player
     * @param amount Amount of giving
     * @param player Player that will take this amount
     */
    public void giveMoney(double amount, Player player) {
        xConomyAPI.changePlayerBalance(player.getUniqueId(), player.getName(), new BigDecimal(amount), true);
    }

    /**
     * Takes money from player
     * @param amount Amount of taken money
     * @param player Player that will lose moneys
     */
    public void takeMoney(double amount, Player player) {
        if (solvent(amount, player)) return;
        xConomyAPI.changePlayerBalance(player.getUniqueId(), player.getName(), new BigDecimal(amount), false);
    }
}
