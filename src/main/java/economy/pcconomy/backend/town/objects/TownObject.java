package economy.pcconomy.backend.town.objects;

import com.palmergames.bukkit.towny.TownyAPI;
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
        this.isNPC = isNPC;
        TownName   = town.getName();

        InitializeNPC();
    }
    public String TownName;
    public boolean isNPC;
    public List<ItemStack> Storage = new ArrayList<>();
    private final double StartBudget = 10000;
    private final int StartStorageAmount = 1650;

    public void InitializeNPC() {
        if (isNPC) { // Хранилище НПС города
            Storage.add(new ItemStack(Material.SPRUCE_WOOD, 100));
            Storage.add(new ItemStack(Material.STONE, 250));
            Storage.add(new ItemStack(Material.GLASS, 50));
            Storage.add(new ItemStack(Material.CARROT, 500));
            Storage.add(new ItemStack(Material.BEEF, 200));
            Storage.add(new ItemStack(Material.IRON_INGOT, 50));
            Storage.add(new ItemStack(Material.COBBLESTONE, 500));

            setBudget(StartBudget);
            LifeCycle();
        }
    }

    public void LifeCycle() {
        if (!isNPC) return;

        if (getLocalInflation() < 1) GetMoneyFromBank(1000); // Печать денег при дефляции
        StorageWorker.CreateResources(100, Storage); // Создание ресурсов с потолком 100 штук
        StorageWorker.UseResources(10, Storage); // Потребление ресурсов
        GenerateLocalPrices(); // Генерация цен для товаров
    }

    public void GenerateLocalPrices() { // Только для НПС города
        for (ItemStack itemStack : Storage) {

            ItemWorker.SetLore(itemStack, "Цена за 1 шт. (Покупка X8):\n" +
                    (Math.round((getBudget() / itemStack.getAmount()) * 100d) / 100d) + CashWorker.currencySigh);
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
        return TownyAPI.getInstance().getTown(TownName).getAccount();
    }
}
