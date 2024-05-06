package economy.pcconomy.backend.economy.town;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class PlayerTown extends Town {
    /**
     * Player town
     * @param town TownyAPI town
     */
    public PlayerTown(com.palmergames.bukkit.towny.object.Town town) {
        TownUUID = town.getUUID();
        Traders  = new ArrayList<>();
    }

    /**
     * Player town
     * @param townUUID Player town UID
     * @param credit Player town credit list
     */
    public PlayerTown(UUID townUUID) {
        TownUUID = townUUID;
        Traders  = new ArrayList<>();
    }

    /**
     * Player town
     * @param townUUID Player town UID
     * @param credit Player town credit list
     * @param traders Traders in player town
     */
    public PlayerTown(UUID townUUID, List<Integer> traders) {
        TownUUID      = townUUID;
        Traders  = traders;
    }
}
