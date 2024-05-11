package economy.pcconomy.backend.economy.bank;

import economy.pcconomy.backend.db.Loadable;


public class BankManager extends Loadable {
    public BankManager(Bank bank) {
        Bank = bank;
    }

    public Bank Bank;

    public Bank getMainBank() {
        return Bank;
    }

    @Override
    public String getName() {
        return "bank_data";
    }
}
