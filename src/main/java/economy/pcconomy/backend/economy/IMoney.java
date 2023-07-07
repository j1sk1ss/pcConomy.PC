package economy.pcconomy.backend.economy;

import economy.pcconomy.backend.economy.objects.Loan;

import java.util.List;

/**
 * IMoney interface of economy objects
 */
public interface IMoney {
    /**
     * Changing budget of target
     * @param amount Amount of changing
     */
    void changeBudget(double amount);

    /**
     * Credit list getter
     * @return Return list of credits
     */
    List<Loan> getCreditList();
}
