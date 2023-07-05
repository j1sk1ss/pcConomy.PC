package economy.pcconomy.backend.economy.town.objects;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.BankAccount;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.IMoney;
import economy.pcconomy.backend.economy.bank.objects.Loan;
import economy.pcconomy.backend.economy.bank.scripts.LoanManager;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.ItemManager;
import economy.pcconomy.backend.economy.town.objects.scripts.StorageManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TownObject implements IMoney {
    public TownObject(Town town, boolean isNPC) {
        this.isNPC = isNPC;
        TownName   = town.getName();
        Credit     = new ArrayList<>();

        if (isNPC) initializeNPC();
    }

    public final String TownName;
    public final List<Loan> Credit;
    public final List<ItemStack> Storage = new ArrayList<>();
    private final int StartStorageAmount = StorageManager.getAmountOfStorage(Storage);
    public boolean isNPC;
    private double previousBudget = 10000;
    public double usefulStorage = PcConomy.Config.getDouble("town.start_useful_storage");
    public double usefulBudget = PcConomy.Config.getDouble("town.start_useful_budget");

    public void initializeNPC() {
        Storage.add(new ItemStack(Material.SPRUCE_WOOD, 100));
        Storage.add(new ItemStack(Material.STONE, 250));
        Storage.add(new ItemStack(Material.GLASS, 170));
        Storage.add(new ItemStack(Material.CARROT, 500));
        Storage.add(new ItemStack(Material.BEEF, 200));
        Storage.add(new ItemStack(Material.IRON_INGOT, 165));
        Storage.add(new ItemStack(Material.COBBLESTONE, 500));

        setBudget(previousBudget);
        lifeCycle();
    }

    public void lifeCycle() {
        LoanManager.takePercentFromBorrowers(this);
        if (!isNPC) return;

        var changePercent = (getBudget() - previousBudget) / previousBudget;
        var isRecession  = (changePercent <= 0 && getLocalInflation() > 0) ? 1 : -1;

        getMoneyFromBank(1000);

        usefulBudget  = usefulStorage - (usefulBudget * Math.abs(changePercent)) * isRecession;
        usefulStorage = usefulBudget + (usefulStorage * Math.abs(changePercent)) * isRecession;

        StorageManager.createResources(500, Storage);
        StorageManager.useResources(100, Storage);

        previousBudget = getBudget();
    }

    public void generateLocalPrices() {
        var budget = getBudget();

        for (ItemStack itemStack : Storage) {
            var amount = itemStack.getAmount() + 1;
            var price = Math.abs(budget / amount);

            ItemManager.setLore(itemStack, "Цена за 1 шт. (Покупка X8):\n" +
                    Math.round(price + (price * PcConomy.GlobalBank.VAT) * 100d) / 100d + CashManager.currencySigh +
                            "\nБез НДС в " + PcConomy.GlobalBank.VAT * 100 + "%:\n" +
                            Math.round(price * 100d) / 1000d + CashManager.currencySigh);
        }
    }

    public double getLocalInflation() {
        return (getBudget() / previousBudget) - ((double) StorageManager.getAmountOfStorage(Storage) / StartStorageAmount);
    }

    public void getMoneyFromBank(double amount) {
        if (amount > PcConomy.GlobalBank.getUsefulAmountOfBudget()) return;

        PcConomy.GlobalBank.BankBudget -= amount;
        changeBudget(amount);
    }

    public void setBudget(double amount) {
        getBankAccount().setBalance(amount, "Economic action");
    }

    public void changeBudget(double amount) {
        getBankAccount().setBalance(getBudget() + amount, "Economic action");
    }

    public List<Loan> getCreditList() {
        return Credit;
    }

    /**
     * Get UUID of all borrowers
     * @return List of borrowers UUID
     */
    public List<UUID> getBorrowers() {
        var list = new ArrayList<UUID>();
        for (var loan : Credit)
            list.add(loan.Owner);

        return list;
    }

    public double getBudget() {
        return getBankAccount().getHoldingBalance();
    }

    public BankAccount getBankAccount() {
        return Objects.requireNonNull(TownyAPI.getInstance().getTown(TownName)).getAccount();
    }
}
