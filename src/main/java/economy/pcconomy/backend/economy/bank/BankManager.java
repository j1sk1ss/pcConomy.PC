package economy.pcconomy.backend.economy.bank;

import economy.pcconomy.backend.db.Loadable;
import lombok.Getter;
import lombok.Setter;


public class BankManager extends Loadable {
    public BankManager(Bank bank) {
        Bank = bank;
    }

    @Getter @Setter public Bank Bank;

    @Override
    public String getName() {
        return "bank_data";
    }
}
