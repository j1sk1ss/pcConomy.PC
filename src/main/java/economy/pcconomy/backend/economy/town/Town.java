package economy.pcconomy.backend.economy.town;

import com.palmergames.bukkit.towny.object.economy.BankAccount;
import economy.pcconomy.backend.economy.IMoney;
import economy.pcconomy.backend.economy.credit.Loan;

import java.util.List;
import java.util.UUID;

public abstract class Town implements IMoney {
    public double quarterlyEarnings;

    public void changeBudget(double amount) {
        quarterlyEarnings += amount;
        getBankAccount().setBalance(getBudget() + amount, "Economic action");
    }

    public abstract UUID getUUID();
    public void setBudget(double amount) {
        getBankAccount().setBalance(amount, "Economic action");
    }
    public double getBudget() {
        return getBankAccount().getHoldingBalance();
    }
    public abstract void lifeCycle();
    public abstract List<Loan> getCreditList();
    public abstract List<UUID> getBorrowers();
    public abstract BankAccount getBankAccount();
}
