package economy.pcconomy.backend.town.objects;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.BankAccount;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.interfaces.IMoney;
import economy.pcconomy.backend.bank.objects.LoanObject;
import economy.pcconomy.backend.bank.scripts.LoanWorker;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.objects.scripts.StorageWorker;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TownObject implements IMoney {
    public TownObject(Town town, boolean isNPC) {
        this.isNPC = isNPC;
        TownName   = town.getName();
        Credit     = new ArrayList<>();

        if (isNPC) InitializeNPC();
    }
    public final String TownName;
    public final List<LoanObject> Credit;
    public final List<ItemStack> Storage = new ArrayList<>();
    private final int StartStorageAmount = StorageWorker.GetAmountOfStorage(Storage);
    public boolean isNPC;
    private double previousBudget = 10000;
    public double usefulStorage = PcConomy.Config.getDouble("town.start_useful_storage");
    public double usefulBudget = PcConomy.Config.getDouble("town.start_useful_budget");

    public void InitializeNPC() {
        Storage.add(new ItemStack(Material.SPRUCE_WOOD, 100));
        Storage.add(new ItemStack(Material.STONE, 250));
        Storage.add(new ItemStack(Material.GLASS, 170));
        Storage.add(new ItemStack(Material.CARROT, 500));
        Storage.add(new ItemStack(Material.BEEF, 200));
        Storage.add(new ItemStack(Material.IRON_INGOT, 165));
        Storage.add(new ItemStack(Material.COBBLESTONE, 500));

        setBudget(previousBudget);
        LifeCycle();
    }

    public void LifeCycle() {
        LoanWorker.takePercentFromBorrowers(this);
        if (!isNPC) return;

        var changePercent = (getBudget() - previousBudget) / previousBudget;
        var isRecession  = (changePercent <= 0 && getLocalInflation() > 0) ? 1 : -1;

        GetMoneyFromBank(1000);

        usefulBudget  = usefulStorage - (usefulBudget * Math.abs(changePercent)) * isRecession;
        usefulStorage = usefulBudget + (usefulStorage * Math.abs(changePercent)) * isRecession;

        StorageWorker.CreateResources(500, Storage);
        StorageWorker.UseResources(100, Storage);

        previousBudget = getBudget();
    }

    public void GenerateLocalPrices() { // Только для НПС города
        var budget = getBudget();

        for (ItemStack itemStack : Storage) {
            var amount = itemStack.getAmount() + 1;
            var price = Math.abs(budget / amount);

            ItemWorker.SetLore(itemStack,
                    "Цена за 1 шт. (Покупка X8):\n" +
                    Math.round(price + (price * PcConomy.GlobalBank.VAT) * 100d) / 100d + CashWorker.currencySigh +
                            "\nБез НДС в " + PcConomy.GlobalBank.VAT * 100 + "%:\n" +
                            Math.round(price * 100d) / 1000d + CashWorker.currencySigh);
        }
    }

    public double getLocalInflation() {
        return (getBudget() / previousBudget) - ((double)StorageWorker.GetAmountOfStorage(Storage) / StartStorageAmount);
    }

    public void GetMoneyFromBank(double amount) {
        if (amount > PcConomy.GlobalBank.GetUsefulAmountOfBudget()) return;

        PcConomy.GlobalBank.BankBudget -= amount;
        ChangeBudget(amount);
    }

    public void setBudget(double amount) {
        getBankAccount().setBalance(amount, "Economic action");
    }

    public void ChangeBudget(double amount) {
        getBankAccount().setBalance(getBudget() + amount, "Economic action");
    }

    public List<LoanObject> GetCreditList() {
        return Credit;
    }

    public double getBudget() {
        return getBankAccount().getHoldingBalance();
    }

    public BankAccount getBankAccount() {
        return TownyAPI.getInstance().getTown(TownName).getAccount();
    }
}
