package economy.pcconomy.backend.bank.objects;

import org.bukkit.entity.Player;

import java.util.UUID;

public class LoanObject {
    public LoanObject(double amount, double percentage, int duration, double dayPayment, Player player) {
        this.amount     = amount;
        this.percentage = percentage;
        this.duration   = duration;
        this.dailyPayment = dayPayment;
        Owner = player.getUniqueId();
    }
    public UUID Owner;
    public double amount;
    public double percentage;
    public int duration;
    public double dailyPayment;

    public int expired;
}
