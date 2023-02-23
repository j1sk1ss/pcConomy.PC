package economy.pcconomy.backend.bank.interfaces;

import economy.pcconomy.backend.bank.objects.LoanObject;

import java.util.List;

public interface IMoney {

    void ChangeBudget(double amount);

    List<LoanObject> GetCreditList();

}
