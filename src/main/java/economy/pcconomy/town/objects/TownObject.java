package economy.pcconomy.town.objects;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.BankAccount;
import economy.pcconomy.PcConomy;
import org.bukkit.inventory.ItemStack;

import java.util.Dictionary;
import java.util.Random;

public class TownObject {
    public TownObject(Town town, boolean isNPC) {
        Town = town;
        this.isNPC = isNPC;
    }

    public Town Town;
    public boolean isNPC;
    public BankObject Bank = PcConomy.GlobalBank;
    public Dictionary<ItemStack, Integer> Storage;
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
        var keys = Storage.keys();

        for (var i = 0; i < Storage.size(); i++) {
            var key = keys.nextElement();
            setAmountOfResource(key, Storage.get(key) + new Random().nextInt() % maxAmount);
        }
    }

    public void UseResources(int maxAmount) {
        if (!isNPC) return;
        var keys = Storage.keys();

        for (var i = 0; i < Storage.size(); i++) {
            var key = keys.nextElement();
            if (Storage.get(key) < 1) continue;
            setAmountOfResource(key, Storage.get(key) - new Random().nextInt() % maxAmount);
        }
    }

    public void GenerateLocalPrices() { // Только для НПС города
        var keys = Storage.keys();

        for (var i = 0; i < Storage.size(); i++) {
            Prices.put(keys.nextElement(), Storage.get(keys.nextElement()) / getBudget());
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

    public void setAmountOfResource(ItemStack itemStack, int amount) {
        Storage.remove(itemStack);
        Storage.put(itemStack, amount);
    }

    public int getAmountOfResource(ItemStack itemStack) {
        return Storage.get(itemStack);
    }

    public int getAmountOfStorage() {
        var keys = Storage.keys();
        var amount = 0;

        for (var i = 0; i < Storage.size(); i++) {
            amount += Storage.get(keys.nextElement());
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
