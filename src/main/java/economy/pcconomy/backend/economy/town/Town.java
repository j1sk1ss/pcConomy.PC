package economy.pcconomy.backend.economy.town;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.economy.BankAccount;

import economy.pcconomy.backend.economy.Capitalist;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


public abstract class Town extends Capitalist {
    /**
     * Quarterly earnings
     */
    public double quarterlyEarnings;
    public List<Integer> traders;

    /**
     * Change budget of town
     * @param amount Amount of changing
     */
    @Override
    public void changeBudget(double amount) {
        quarterlyEarnings += amount;
        getBankAccount().setBalance(getBudget() + amount, "PcConomy economic action");
    }

    /**
     * UUID of town
     * @return UUID
     */
    public abstract UUID getUUID();

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
}
