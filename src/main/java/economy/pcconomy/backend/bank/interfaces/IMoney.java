package economy.pcconomy.backend.bank.interfaces;

import economy.pcconomy.backend.bank.objects.Loan;

import java.util.List;

public interface IMoney {
    /**
     * Changing budget of target
     * @param amount Amount of changing
     */
    void ChangeBudget(double amount);

    /**
     * Credit list getter
     * @return Return list of credits
     */
    List<Loan> GetCreditList();
}
