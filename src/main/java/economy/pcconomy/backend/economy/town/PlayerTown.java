package economy.pcconomy.backend.economy.town;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.economy.BankAccount;
import economy.pcconomy.backend.economy.credit.Loan;
import economy.pcconomy.backend.economy.credit.scripts.LoanManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerTown extends Town {
    /**
     * Player town
     * @param town TownyAPI town
     */
    public PlayerTown(com.palmergames.bukkit.towny.object.Town town) {
        TownUUID   = town.getUUID();
        Credit     = new ArrayList<>();
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
