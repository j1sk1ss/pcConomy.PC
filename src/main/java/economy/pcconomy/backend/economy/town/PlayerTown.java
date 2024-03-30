package economy.pcconomy.backend.economy.town;

import economy.pcconomy.backend.economy.credit.Loan;
import economy.pcconomy.backend.economy.credit.scripts.LoanManager;

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
        Credit   = new ArrayList<>();
    }

    /**
     * Player town
     * @param townUUID Player town UID
     * @param credit Player town credit list
     */
    public PlayerTown(UUID townUUID, List<Loan> credit) {
        TownUUID = townUUID;
        Credit = credit;
    }

    public final UUID TownUUID;
    public final List<Loan> Credit;

    @Override
    public UUID getUUID() {
        return TownUUID;
    }

    @Override
    public void newDay() {
        LoanManager.takePercentFromBorrowers(this);
    }

    @Override
    public List<Loan> getCreditList() {
        return Credit;
    }
}
