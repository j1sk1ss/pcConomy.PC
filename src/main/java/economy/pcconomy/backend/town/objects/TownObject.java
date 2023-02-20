package economy.pcconomy.backend.town.objects;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.BankAccount;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.objects.BorrowerObject;
import economy.pcconomy.backend.bank.objects.LoanObject;
import economy.pcconomy.backend.bank.scripts.LoanWorker;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.objects.scripts.StorageWorker;
import economy.pcconomy.backend.town.scripts.TownWorker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TownObject {
    public TownObject(Town town, boolean isNPC) {
        this.isNPC = isNPC;
        TownName   = town.getName();
        Credit     = new ArrayList<>();

        if (isNPC) InitializeNPC();
    }
    public String TownName;
    public List<LoanObject> Credit;
    public boolean isNPC;
    public List<ItemStack> Storage = new ArrayList<>();
    private final double StartBudget = 10000;
    private final int StartStorageAmount = StorageWorker.getAmountOfStorage(Storage);

    public void InitializeNPC() {
        Storage.add(new ItemStack(Material.SPRUCE_WOOD, 100));
        Storage.add(new ItemStack(Material.STONE, 250));
        Storage.add(new ItemStack(Material.GLASS, 170));
        Storage.add(new ItemStack(Material.CARROT, 500));
        Storage.add(new ItemStack(Material.BEEF, 200));
        Storage.add(new ItemStack(Material.IRON_INGOT, 165));
        Storage.add(new ItemStack(Material.COBBLESTONE, 500));

        setBudget(StartBudget);
        LifeCycle();
    }

    public void LifeCycle() {
        TakePercentFromBorrowers();
        if (!isNPC) return;

        if (getLocalInflation() < .5) GetMoneyFromBank(1000); // Взятие кредита при дефляции
        else StorageWorker.CreateResources(500, Storage); // Создание ресурсов с потолком 100 штук

        StorageWorker.UseResources(100, Storage); // Потребление ресурсов
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
        return (getBudget() / StartBudget) - ((double)StorageWorker.getAmountOfStorage(Storage) / StartStorageAmount);
    }

    public void GetMoneyFromBank(double amount) {
        if (amount > PcConomy.GlobalBank.GetUsefulAmountOfBudget()) return;

        PcConomy.GlobalBank.BankBudget -= amount;
        changeBudget(amount);
    }

    public void CreateLoan(double amount, int duration, Player player) {
        // Создание кредита на игрока
        var percentage = LoanWorker.getPercent(amount, duration); // процент по кредиту
        var dailyPayment = LoanWorker.getDailyPayment(amount, duration, percentage); // дневной платёж

        Credit.add(new LoanObject(amount + amount * percentage, percentage, duration, dailyPayment, player));

        changeBudget(-amount);
        new BalanceWorker().GiveMoney(amount, player);
    }

    public void TakePercentFromBorrowers() {
        // Взятие процента со счёта игрока
        for (LoanObject loanObject : Credit) {
            if (loanObject.amount <= 0) {
                DestroyLoan(loanObject.Owner);
                return;
            }

            var balanceWorker = new BalanceWorker();
            if (balanceWorker.isSolvent(loanObject.dailyPayment, Bukkit.getPlayer(loanObject.Owner)))
                loanObject.expired += 1;

            balanceWorker.TakeMoney(loanObject.dailyPayment, Bukkit.getPlayer(loanObject.Owner));
            loanObject.amount -= loanObject.dailyPayment;

            changeBudget(loanObject.dailyPayment);
        }
    }

    public LoanObject GetLoan(UUID player) {
        for (LoanObject loan:
                Credit) {
            if (loan.Owner.equals(player)) return loan;
        }

        return null;
    }

    public void DestroyLoan(UUID player) {
        // Закрытие кредита
        var loan = GetLoan(player);

        var borrower = PcConomy.GlobalBorrowerWorker.getBorrowerObject(Bukkit.getPlayer(player));
        if (borrower != null) {
            borrower.CreditHistory.add(loan);
            PcConomy.GlobalBorrowerWorker.setBorrowerObject(borrower);
        } else {
            PcConomy.GlobalBorrowerWorker.borrowerObjects.add(new BorrowerObject(Bukkit.getPlayer(player), loan));
        }

        Credit.remove(GetLoan(player));
    }

    public void setBudget(double amount) {
        getBankAccount().setBalance(amount, "Economic action");
    }

    public void changeBudget(double amount) {
        getBankAccount().setBalance(getBudget() + amount, "Economic action");
    }

    public double getBudget() {
        return getBankAccount().getHoldingBalance();
    }

    public BankAccount getBankAccount() {
        return TownyAPI.getInstance().getTown(TownName).getAccount();
    }
}
