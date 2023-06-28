package economy.pcconomy.backend.economy;

import economy.pcconomy.backend.economy.bank.objects.Loan;

import java.util.List;

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
