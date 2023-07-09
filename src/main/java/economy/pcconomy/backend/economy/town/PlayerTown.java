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
        TownName   = town.getName();
        Credit     = new ArrayList<>();
    }

    public final String TownName;
    public final List<Loan> Credit;

    @Override
    public String getName() {
        return TownName;
    }

    @Override
    public void lifeCycle() {
        LoanManager.takePercentFromBorrowers(this);
    }

    @Override
    public List<Loan> getCreditList() {
        return Credit;
    }

    @Override
    public List<UUID> getBorrowers() {
        var list = new ArrayList<UUID>();
        for (var loan : Credit)
            list.add(loan.Owner);

        return list;
    }

    @Override
    public BankAccount getBankAccount() {
        return Objects.requireNonNull(TownyAPI.getInstance().getTown(TownName)).getAccount();
    }
}
