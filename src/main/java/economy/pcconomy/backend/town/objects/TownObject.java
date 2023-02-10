package economy.pcconomy.backend.town.objects;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.BankAccount;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.objects.scripts.StorageWorker;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TownObject {
    public TownObject(Town town, boolean isNPC) {
        Town = town;
        this.isNPC = isNPC;

        if (isNPC) { // Хранилище НПС города
            Storage.add(new ItemStack(Material.SPRUCE_WOOD, 1000));
            Storage.add(new ItemStack(Material.STONE, 2500));
            Storage.add(new ItemStack(Material.GLASS, 500));
            Storage.add(new ItemStack(Material.CARROT, 5000));
            Storage.add(new ItemStack(Material.BEEF, 2000));
            Storage.add(new ItemStack(Material.IRON_INGOT, 500));
            Storage.add(new ItemStack(Material.COBBLESTONE, 5000));

            setBudget(StartBudget);
            LifeCycle();
        }
    }

    public Town Town;
    public boolean isNPC;
    public List<ItemStack> Storage = new ArrayList<>();
    private final double StartBudget = 40000;
    private final int StartStorageAmount = 16500;

    public void LifeCycle() {
        if (!isNPC) return;

        if (getLocalInflation() < 1) GetMoneyFromBank(100); // Печать денег при дефляции
        StorageWorker.CreateResources(100, Storage); // Создание ресурсов с потолком 100 штук
        StorageWorker.UseResources(10, Storage); // Потребление ресурсов
        GenerateLocalPrices(); // Генерация цен для товаров
    }

    public void GenerateLocalPrices() { // Только для НПС города
        for (ItemStack itemStack : Storage) {
            ItemWorker.SetLore(itemStack, "Цена за 1 шт. (Покупка X8):\n" +
                    (Math.round(getBudget() / itemStack.getAmount() * 100d) / 100d) + CashWorker.currencySigh);
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