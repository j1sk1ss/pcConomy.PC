package economy.pcconomy.backend.cash;

import me.yic.xconomy.api.XConomyAPI;
import org.bukkit.entity.Player;

import java.math.BigDecimal;


public class Balance {
    private static final XConomyAPI xConomyAPI = new XConomyAPI();

    /**
     * Give value of balance of Player
     * @param player Player that balance should be revealed
     * @return Balance
     */
    public static double getBalance(Player player) {
        return xConomyAPI.getPlayerData(player.getUniqueId()).getBalance().doubleValue();
    }

    /**
     * Check is player have moneys
     * @param value Value
     * @param player Player
     * @return Status
     */
    public static boolean solvent(Player player, double value) {
        return xConomyAPI.getPlayerData(player.getUniqueId()).getBalance().compareTo(new BigDecimal(value)) < 0;
    }

    /**
     * Gives money for player
     * @param amount Amount of giving
     * @param player Player that will take this amount
     */
    public static void giveMoney(Player player, double amount) {
        xConomyAPI.changePlayerBalance(player.getUniqueId(), player.getName(), new BigDecimal(amount), true);
    }

    /**
     * Takes money from player
     * @param amount Amount of taken money
     * @param player Player that will lose moneys
     */
    public static void takeMoney(Player player, double amount) {
        if (solvent(player, amount)) return;
        xConomyAPI.changePlayerBalance(player.getUniqueId(), player.getName(), new BigDecimal(amount), false);
    }
}
