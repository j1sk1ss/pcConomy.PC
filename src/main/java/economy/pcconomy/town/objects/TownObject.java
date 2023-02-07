package economy.pcconomy.town.objects;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.BankAccount;
import economy.pcconomy.PcConomy;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Dictionary;
import java.util.List;
import java.util.Random;

public class TownObject {
    public TownObject(Town town, boolean isNPC) {
        Town = town;
        this.isNPC = isNPC;
    }

    public Town Town;
    public boolean isNPC;
    public double Margin;
    public economy.pcconomy.bank.Bank Bank = PcConomy.GlobalBank;
    public List<ItemStack> Storage;
    public Dictionary<ItemStack, Double> Prices;
    private final double StartBudget = 100;
    private final int StartStorageAmount = 100;

    public void LifeCycle() {
        if (!isNPC) return;

        if (getLocalInflation() < 1) GetMoneyFromBank(100); // Печать денег при дефляции
        CreateResources(100); // Создание ресурсов с потолком 100 штук
        UseResources(10); // Потребление ресурсов
        GenerateLocalPrices(); // Генерация цен для товаров
    }

    public void CreateResources(int maxAmount) { // Только для НПС города
        if (!isNPC) return;

        for (ItemStack item:
             Storage) {
            setAmountOfResource(item, getAmountOfResource(item) + new Random().nextInt() % maxAmount);
        }
    }

    public void UseResources(int maxAmount) {
        if (!isNPC) return;

        for (ItemStack item:
                Storage) {
            if (item.getAmount() < 10) return;
            setAmountOfResource(item, getAmountOfResource(item) - new Random().nextInt() % maxAmount);
        }
    }

    public void GenerateLocalPrices() { // Только для НПС города
        for (ItemStack itemStack : Storage) {
            Prices.put(itemStack, itemStack.getAmount() / getBudget());
        }
    }

    public double getLocalInflation() {
        return ((double)getAmountOfStorage() / StartStorageAmount) - (getBudget() / StartBudget);
    }

    public void GetMoneyFromBank(double amount) {
        if (amount > Bank.GetUsefulAmountOfBudget()) return;

        Bank.BankBudget -= amount;
        setBudget(getBudget() + amount);
    }

    public void setAmountOfResource(ItemStack item, int amount) {
        for (ItemStack itemStack:
             Storage) {
            if (itemStack.isSimilar(item)) {
                Storage.set(Storage.indexOf(itemStack), new ItemStack(item.getType(), amount));
            }
        }
    }

    public int getAmountOfResource(ItemStack item) {
        for (ItemStack itemStack:
                Storage) {
            if (itemStack.isSimilar(item)) {
                return itemStack.getAmount();
            }
        }
        return 0;
    }

    public ItemStack getResource(ItemStack item) {
        for (ItemStack itemStack:
                Storage) {
            if (itemStack.isSimilar(item)) {
                return itemStack;
            }
        }
        return null;
    }

    public int getAmountOfStorage() {
        var amount = 0;

        for (var i = 0; i < Storage.size(); i++) {
            amount += Storage.get(i).getAmount();
        }
        return amount;
    }

    public void setBudget(double amount) {
        getBankAccount().setBalance(amount, "Economic action");
    }

    public double getBudget() {
        return getBankAccount().getHoldingBalance();
    }

    public BankAccount getBankAccount() {
        return Town.getAccount();
    }
}
