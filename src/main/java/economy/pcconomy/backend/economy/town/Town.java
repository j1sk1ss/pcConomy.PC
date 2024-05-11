package economy.pcconomy.backend.economy.town;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.economy.BankAccount;

import economy.pcconomy.backend.economy.Capitalist;
import economy.pcconomy.backend.economy.credit.Loan;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class Town extends Capitalist {
    /**
     * Quarterly earnings
     */
    public double QuarterlyEarnings;
    public List<Integer> Traders;
    public UUID TownUUID;

    /**
     * Change budget of town
     * @param amount Amount of changing
     */
    @Override
    public void changeBudget(double amount) {
        QuarterlyEarnings += amount;
        getBankAccount().setBalance(getBudget() + amount, "PcConomy economic action");
    }

    /**
     * UUID of town
     * @return UUID
     */
    public UUID getUUID() {
        return TownUUID;
    }

    /**
     * Set budget of town
     * @param amount Amount of budget
     */
    public void setBudget(double amount) {
        getBankAccount().setBalance(amount, "PcConomy economic action");
    }

    /**
     * Get budget of town
     * @return Budget
     */
    public double getBudget() {
        return getBankAccount().getHoldingBalance();
    }

    /**
     * Get bank account from towny API
     * @return Bank account
     */
    public BankAccount getBankAccount() {
        return Objects.requireNonNull(TownyAPI.getInstance().getTown(getUUID())).getAccount();
    }

    @Override
    public void newDay() {
        throw new UnsupportedOperationException("Unimplemented method 'newDay'");
    }

    @Override
    public List<Loan> getCreditList() {
        throw new UnsupportedOperationException("Unimplemented method 'getCreditList'");
    }

    @Override
    public double getTrustCoefficient() { return 0; }
}
