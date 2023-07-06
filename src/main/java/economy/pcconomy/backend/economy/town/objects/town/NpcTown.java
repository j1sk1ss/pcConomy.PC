package economy.pcconomy.backend.economy.town.objects.town;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.economy.BankAccount;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.economy.bank.objects.Loan;
import economy.pcconomy.backend.economy.bank.scripts.LoanManager;
import economy.pcconomy.backend.economy.town.objects.scripts.StorageManager;
import economy.pcconomy.backend.scripts.items.ItemManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NpcTown extends Town {
    /**
     * Npc town
     * @param town TownyAPI town
     */
    public NpcTown(com.palmergames.bukkit.towny.object.Town town) {
        TownName   = town.getName();
        Credit     = new ArrayList<>();

        Storage = Arrays.asList(
                new ItemStack(Material.SPRUCE_WOOD, 100),
                new ItemStack(Material.STONE, 250),
                new ItemStack(Material.GLASS, 170),
                new ItemStack(Material.CARROT, 500),
                new ItemStack(Material.BEEF, 200),
                new ItemStack(Material.IRON_INGOT, 165),
                new ItemStack(Material.COBBLESTONE, 500)
        );

        StartStorageAmount = StorageManager.getAmountOfStorage(Storage);

        setBudget(previousBudget);
        lifeCycle();
    }

    public double usefulStorage = PcConomy.Config.getDouble("town.start_useful_storage");
    public double usefulBudget = PcConomy.Config.getDouble("town.start_useful_budget");
    public final List<ItemStack> Storage;
    public final String TownName;
    public final List<Loan> Credit;
    private double previousBudget = 10000;
    private final int StartStorageAmount;

    /**
     * Buy resources from town storage
     * @param itemStack Item that was chosen
     * @param buyer  Player who want by this item
     */
    public void buyResourceFromStorage(ItemStack itemStack, Player buyer) {
        var itemAmount = 8;
        var price = ItemManager.getPriceFromLore(itemStack, 1) * itemAmount;

        if (StorageManager.getAmountOfResource(itemStack, Storage) * usefulStorage < itemAmount) {
            buyer.sendMessage("Извините, но данного товара и у нас самих не очень много.");
            return;
        }

        var cash = new CashManager();
        if (cash.amountOfCashInInventory(buyer) < price) return;

        cash.takeCashFromInventory(price, buyer);
        changeBudget(price / PcConomy.GlobalBank.VAT);
        PcConomy.GlobalBank.BankBudget += (price - price / PcConomy.GlobalBank.VAT);
        ItemManager.giveItems(new ItemStack(itemStack.getType(), itemAmount), buyer);
        StorageManager.setAmountOfResource(itemStack, StorageManager.getAmountOfResource(itemStack, Storage) - itemAmount, Storage);

        generateLocalPrices();
    }

    /**
     * Sell resource to town
     * @param itemStack Item that will be sold
     * @param seller Player who sell item
     */
    public void sellResourceToStorage(ItemStack itemStack, Player seller) {
        var itemAmount = itemStack.getAmount();
        var resource = StorageManager.getResource(itemStack, Storage);
        if (resource == null) {
            seller.sendMessage("Такой товар мы не принимаем.");
            return;
        }

        var price = ItemManager.getPriceFromLore(resource, 1) * itemAmount;
        if (price > getBudget() * usefulBudget) {
            seller.sendMessage("Слишком дорого для нашего города.");
            return;
        }

        var cash  = new CashManager();

        seller.getInventory().setItemInMainHand(null);
        StorageManager.setAmountOfResource(itemStack, StorageManager.getAmountOfResource(itemStack, Storage) + itemAmount, Storage);
        cash.giveCashToPlayer(price / PcConomy.GlobalBank.VAT, seller);
        PcConomy.GlobalBank.BankBudget += (price - price / PcConomy.GlobalBank.VAT);
        changeBudget(-price);

        generateLocalPrices();
    }

    /**
     * Generate prices for stuff
     */
    public void generateLocalPrices() {
        var budget = getBudget();

        for (var itemStack : Storage) {
            var amount = itemStack.getAmount() + 1;
            var price = Math.abs(budget / amount);

            ItemManager.setLore(itemStack, "Цена за 1 шт. (Покупка X8):\n" +
                    Math.round(price + (price * PcConomy.GlobalBank.VAT) * 100d) / 100d + CashManager.currencySigh +
                    "\nБез НДС в " + PcConomy.GlobalBank.VAT * 100 + "%:\n" +
                    Math.round(price * 100d) / 1000d + CashManager.currencySigh);
        }
    }

    /**
     * Get town inflation
     * @return Local inflation
     */
    public double getLocalInflation() {
        return (getBudget() / previousBudget) - ((double) StorageManager.getAmountOfStorage(Storage) / StartStorageAmount);
    }

    /**
     * Take moneys from bank
     * @param amount Amount of taken moneys
     */
    public void getMoneyFromBank(double amount) {
        if (amount > PcConomy.GlobalBank.getUsefulAmountOfBudget()) return;

        PcConomy.GlobalBank.BankBudget -= amount;
        changeBudget(amount);
    }

    @Override
    public String getName() {
        return TownName;
    }

    @Override
    public void lifeCycle() {
        LoanManager.takePercentFromBorrowers(this);

        var changePercent = (getBudget() - previousBudget) / previousBudget;
        var isRecession  = (changePercent <= 0 && getLocalInflation() > 0) ? 1 : -1;

        getMoneyFromBank(1000);

        usefulBudget  = usefulStorage - (usefulBudget * Math.abs(changePercent)) * isRecession;
        usefulStorage = usefulBudget + (usefulStorage * Math.abs(changePercent)) * isRecession;

        StorageManager.createResources(500, Storage);
        StorageManager.useResources(100, Storage);

        previousBudget = getBudget();
    }

    @Override
    public List<Loan> getCreditList() {
        return Credit;
    }

    @Override
    public List<UUID> getBorrowers() {
        var list = new ArrayList<UUID>();
        for (var loan : Credit)
            list.add(loan.Owner);

        return list;
    }

    @Override
    public BankAccount getBankAccount() {
        return Objects.requireNonNull(TownyAPI.getInstance().getTown(TownName)).getAccount();
    }
}
