package economy.pcconomy.backend.economy;

import economy.pcconomy.backend.economy.credit.Loan;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public abstract class Capitalist {
    public abstract void newDay();

    public abstract void changeBudget(double var1);

    public abstract List<Loan> getCreditList();

    public abstract double getTrustCoefficient();

    public List<UUID> getBorrowers() {
        var list = new ArrayList<UUID>();
        for (var user : getCreditList()) {
            list.add(user.getOwner());
        }

        return list;
    }
}
