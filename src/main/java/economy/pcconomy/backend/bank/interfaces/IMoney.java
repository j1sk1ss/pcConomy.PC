package economy.pcconomy.backend.bank.interfaces;

import economy.pcconomy.backend.bank.objects.LoanObject;

import java.util.List;

public interface IMoney {

    public void ChangeBudget(double amount);

    public List<LoanObject> GetCreditList();

}
