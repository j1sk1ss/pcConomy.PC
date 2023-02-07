package economy.pcconomy.bank.objects;

public class LoanObject {
    public LoanObject(double amount, double percentage, int duration, double dayPayment) {
        this.amount     = amount;
        this.percentage = percentage;
        this.duration   = duration;
        this.dailyPayment = dayPayment;
    }
    public double amount;
    public double percentage;
    public int duration;
    public double dailyPayment;
}
