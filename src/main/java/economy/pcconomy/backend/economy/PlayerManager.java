package economy.pcconomy.backend.economy;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import com.palmergames.bukkit.towny.TownyAPI;

import java.util.List;
import java.util.ArrayList;


public class PlayerManager {
    /**
     * Get country mens of player (All players that live with this player in the same town)
     * @param player Player
     * @return Country mens
     */
    public static List<Player> getCountryMens(Player player) {
        var town = TownyAPI.getInstance().getTown(player);
        if (town == null) return new ArrayList<>();

        var players = new ArrayList<Player>();
        for (var resident : town.getResidents())
            players.add(resident.getPlayer());

        return players;
    }

    /**
     * Get count of minecraft days on server
     * @param player Player
     * @return Count of minecraft days on server
     */
    public static int getPlayerServerDuration(Player player) {
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
    }
}
