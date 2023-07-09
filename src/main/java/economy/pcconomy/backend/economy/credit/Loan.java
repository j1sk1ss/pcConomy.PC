package economy.pcconomy.backend.economy.credit;

import org.bukkit.entity.Player;

import java.util.UUID;

public class Loan {
    /**
     * Loan object
     * @param amount Amount what was got as loan
     * @param percentage Percent of loan
     * @param duration Duration of loan
     * @param dayPayment Daily payment
     * @param player Loan owner
     */
    public Loan(double amount, double percentage, int duration, double dayPayment, Player player) {
        this.amount       = amount;
        this.percentage   = percentage;
        this.duration     = duration;
        this.dailyPayment = dayPayment;
        Owner             = player.getUniqueId();
    }

    public final UUID Owner;
    public double amount;
    public final double percentage;
    public final int duration;
    public final double dailyPayment;
    public int expired;
}
