package economy.pcconomy.backend.economy;

import economy.pcconomy.backend.economy.credit.Loan;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * IMoney interface of economy objects
 */
public abstract class Capitalist {
    /**
     * Life cycle
     */
    public abstract void newDay();

    /**
     * Changing budget of target
     * @param amount Amount of changing
     */
    public abstract void changeBudget(double amount);

    /**
     * Credit list getter
     * @return Return list of credits
     */
    public abstract List<Loan> getCreditList();

    /**
     * Get UUID of all borrowers
     * @return List of borrowers UUID
     */
    public List<UUID> getBorrowers() {
        var list = new ArrayList<UUID>();
        for (var loan : getCreditList())
            list.add(loan.Owner);

        return list;
    }
}
