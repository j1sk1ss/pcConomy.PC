package economy.pcconomy.backend.economy.bank;

import lombok.Getter;
import lombok.Setter;
import economy.pcconomy.backend.db.Loadable;


@Setter
@Getter
public class BankManager extends Loadable {
    public BankManager(Bank bank) {
        Bank = bank;
    }

    public Bank Bank;

    @Override
    public String getName() {
        return "bank_data";
    }
}
