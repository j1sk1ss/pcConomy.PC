package economy.pcconomy.backend.town.objects;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.BankAccount;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.town.objects.scripts.StorageWorker;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

public class TownObject {
    public TownObject(Town town, boolean isNPC) {
        Town = town;
        this.isNPC = isNPC;

        if (isNPC) { // Хранилище НПС города
            Storage = Arrays.asList(
                    new ItemStack(Material.SPRUCE_WOOD, 100),
                    new ItemStack(Material.STONE, 250),
                    new ItemStack(Material.GLASS, 50),
                    new ItemStack(Material.CARROT, 500),
                    new ItemStack(Material.BEEF, 200),
                    new ItemStack(Material.IRON_INGOT, 50),
                    new ItemStack(Material.COBBLESTONE, 500)
            );
        }
    }

    public Town Town;
    public boolean isNPC;
    public double Margin = .2d;
    public List<ItemStack> Storage;
    public Dictionary<ItemStack, Double> Prices;
    private final double StartBudget = 5000;
    private final int StartStorageAmount = 1650;

    public void LifeCycle() {
        if (!isNPC) return;

        if (getLocalInflation() < 1) GetMoneyFromBank(100); // Печать денег при дефляции
        StorageWorker.CreateResources(100, Storage); // Создание ресурсов с потолком 100 штук
        StorageWorker.UseResources(10, Storage); // Потребление ресурсов
        GenerateLocalPrices(); // Генерация цен для товаров
    }

    public void GenerateLocalPrices() { // Только для НПС города
        for (ItemStack itemStack : Storage) {
            Prices.put(itemStack, itemStack.getAmount() / getBudget());
        }
    }

    public double getLocalInflation() {
        return ((double)StorageWorker.getAmountOfStorage(Storage) / StartStorageAmount) - (getBudget() / StartBudget);
    }

    public void GetMoneyFromBank(double amount) {
        if (amount > PcConomy.GlobalBank.GetUsefulAmountOfBudget()) return;

        PcConomy.GlobalBank.BankBudget -= amount;
        setBudget(getBudget() + amount);
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
